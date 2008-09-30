package org.glite.authz.pap.repository;

import java.io.File;
import java.util.List;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
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

    public static DAOFactory getDAOFactory() {
        return DAOFactory.getDAOFactory();
    }

    public static RepositoryManager getInstance() {
        return new FileSystemRepositoryManager();
    }

    public void bootstrap() {
        
        initialize();
        
        PAPManager.getInstance().createLocalPAPIfNotExists();
        
        setLocalPoliciesFromConfigurationFile();
        
        FillRepository.fillFromConfiguration();
    }
    
    // Temporary method for initializing the repository
    private void setLocalPoliciesFromConfigurationFile() {
        PolicyFileEncoder pse = new PolicyFileEncoder();

        File policyConfigurationFile;
        try {
            policyConfigurationFile = new File(PAPConfiguration.instance().getPapPolicyConfigurationFileName());
        } catch (PAPConfigurationException e) {
            policyConfigurationFile = new File("/opt/glite/etc/pap/pap_policy.ini");
        }
        
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

    protected abstract void initialize();

}
