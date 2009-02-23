package org.glite.authz.pap.repository;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemRepositoryManager;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RepositoryManager {
    
    private static final Logger log = LoggerFactory.getLogger(RepositoryManager.class);
    private static boolean initialized = false;
    
    protected RepositoryManager() {}
    
    /**
     * Call the bootstrap() method before using this class.
     */

    public static void bootstrap() {
        
        FileSystemRepositoryManager.initialize();
        
        initialized = true;
        
        // FillRepository.fillFromConfiguration();
    }

    public static DAOFactory getDAOFactory() {
        return DAOFactory.getDAOFactory();
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
        
        List<XACMLObject> policyList;
        try {
            policyList = pse.parse(policyConfigurationFile);
        } catch (EncodingException e) {
            throw new RepositoryException(e);
        }
        
        PAPContainer localPapContainer = PAPManager.getInstance().getLocalPAPContainer();
        
        localPapContainer.deleteAllPolicies();
        localPapContainer.deleteAllPolicySets();
        
        for (XACMLObject xacmlObject:policyList) {
            
            if (xacmlObject instanceof PolicySetType)
                localPapContainer.storePolicySet((PolicySetType) xacmlObject);
            else
                localPapContainer.storePolicy((PolicyType) xacmlObject);
        }
        
    }
}
