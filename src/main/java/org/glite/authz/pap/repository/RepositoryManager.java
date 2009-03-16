package org.glite.authz.pap.repository;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.XACMLWizard;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemRepositoryManager;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RepositoryManager {

	public static final String REPOSITORY_VERSION = "1";
    private static boolean initialized = false;
    private static final Logger log = LoggerFactory.getLogger(RepositoryManager.class);

    protected RepositoryManager() {}

    /**
     * Call the bootstrap() method before using this class.
     */

    public static void bootstrap() {

        FileSystemRepositoryManager.initialize();
        
        String repositoryVersion = FileSystemRepositoryManager.getVersion();
        
        if (!(REPOSITORY_VERSION.equals(repositoryVersion))) {
        	throw new InvalidVersionException("Invalid repository version (v" + repositoryVersion
        		+ "). Requested version is v" + REPOSITORY_VERSION);
		}

		log.info("Repository version: v" + repositoryVersion);
		
        initialized = true;
    }
    
    public static DAOFactory getDAOFactory() {
        return DAOFactory.getDAOFactory();
    }

    public static String getVersion() {
    	return REPOSITORY_VERSION;
    }

    /**
     * Initialize the repository reading policies from the configuration file.
     */
    public static void setLocalPoliciesFromConfigurationFileIfSetIntoConfiguration() {

        if (!initialized) {
            throw new RepositoryException("Trying to use the repository before initilization. Please use the bootstrap() method.");
        }

        boolean usePolicyConfigFile;

        try {
            usePolicyConfigFile = PAPConfiguration.instance().getBoolean("use-policy-config-file");
        } catch (NoSuchElementException e) {
            usePolicyConfigFile = false;
        }

        if (!usePolicyConfigFile) {
            return;
        }

        PolicyFileEncoder pse = new PolicyFileEncoder();

        File policyConfigurationFile = new File(PAPConfiguration.instance().getPapPolicyConfigurationFileName());

        log.info("Reading policy configuration file: " + policyConfigurationFile.getAbsolutePath());

        if (!policyConfigurationFile.exists()) {
            log.info("Policy configuration file not found... leaving repository empty.");
            return;
        }

        List<XACMLWizard> wizardList;
        try {
            wizardList = pse.parse(policyConfigurationFile);
        } catch (EncodingException e) {
            throw new RepositoryException(e);
        }

        PAPContainer localPapContainer = PAPManager.getInstance().getLocalPAPContainer();

        localPapContainer.deleteAllPolicies();
        localPapContainer.deleteAllPolicySets();

        for (XACMLWizard xacmlWizard : wizardList) {

            if (!(xacmlWizard instanceof PolicySetWizard)) {
                EncodingException e = new EncodingException("\"action\" element is allowed only inside a \"resource\" element");
                throw new RepositoryException(e);
            }

            PolicySetWizard policySetWizard = (PolicySetWizard) xacmlWizard;

            localPapContainer.addPolicySet(-1, policySetWizard.getXACML());

            for (PolicyWizard policyWizard : policySetWizard.getPolicyWizardList()) {
                localPapContainer.storePolicy(policyWizard.getXACML());
                policyWizard.releaseChildrenDOM();
                policyWizard.releaseDOM();
            }
        }
    }
}
