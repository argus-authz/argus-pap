package org.glite.authz.pap.repository;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemPAPManager;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemRepositoryManager;
import org.opensaml.xacml.policy.PolicySetType;

public abstract class RepositoryManager {

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
        
        PAPManager papManager = getPAPManager();
        PAP localPAP = PAP.makeLocalPAP();
        
        if (!papManager.exists(localPAP)) {
            
            papManager.create(localPAP);
            
            PolicySetType localPolicySet = PolicySetHelper.buildWithAnyTarget(localPAP.getPapId(),
                    PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
            
            PAPContainer local = papManager.get(localPAP);
            local.storePolicySet(localPolicySet);
        }
    }

    protected abstract void initialize();

}
