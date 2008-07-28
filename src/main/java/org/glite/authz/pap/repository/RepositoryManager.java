package org.glite.authz.pap.repository;

import java.io.File;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemPAPManager;
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

    public static PAPManager getPAPManager() {
        return FileSystemPAPManager.getInstance();
    }

    public void bootstrap() {
        
        initialize();
        
        createLocalPAP();
        
        setLocalPoliciesFromConfigurationFile();
        
        FillRepository.fillFromConfiguration();
    }
    
    private void createLocalPAP() {
        
        PAPManager papManager = getPAPManager();
        PAP localPAP = PAP.makeLocalPAP();
        
        if (!papManager.exists(localPAP)) {
            
            papManager.create(localPAP);
            
            PolicySetType localPolicySet = PolicySetHelper.buildWithAnyTarget(localPAP.getPapId(),
                    PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
            
            PAPContainer localPapContainer = papManager.getContainer(localPAP);
            localPapContainer.storePolicySet(localPolicySet);
        }
    }
    
    // Temporary method for initializing the repository
    private void setLocalPoliciesFromConfigurationFile() {
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
        
        PAPContainer localPapContainer = getPAPManager().getContainer(PAP.makeLocalPAP());
        
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
