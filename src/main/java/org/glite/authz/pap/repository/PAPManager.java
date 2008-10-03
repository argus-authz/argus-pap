package org.glite.authz.pap.repository;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.distribution.DistributionConfiguration;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemPAPManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicySetType;

public abstract class PAPManager {
    
    private static PAPManager instance = null;
    protected List<PAP> papList;
    protected static PAP localPAP = PAP.makeLocalPAP();
    protected DistributionConfiguration distributionConfiguration;
    
    public static PAPManager getInstance() {
        if (instance == null)
            instance = new FileSystemPAPManager();
        return instance;
    }
    
    protected PAPManager() {
        distributionConfiguration = DistributionConfiguration.getInstance();
        papList = distributionConfiguration.getRemotePAPList();
    }

    public abstract PAPContainer add(PAP pap);
    
    public void createLocalPAPIfNotExists() {
        if (exists(localPAP.getPapId()))
            return;
            
        add(localPAP);
        
        PolicySetType localPolicySet = PolicySetHelper.buildWithAnyTarget(localPAP.getPapId(),
                PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
        getLocalPAPContainer().storePolicySet(localPolicySet);
    }
    
    public abstract PAP delete(String papId);
    
    public abstract boolean exists(String papId);

    public abstract PAP get(String papId);

    public abstract List<PAP> getAll();

    public abstract PAPContainer getContainer(String papId);

    public abstract List<PAPContainer> getContainerAll();
    
    public PAP getLocalPAP() {
        return localPAP;
    }
    
    public PAPContainer getLocalPAPContainer() {
        if (!exists(localPAP.getPapId()))
            throw new NotFoundException("Critical error (probably a BUG): local PAP not found.");
        return new PAPContainer(localPAP);
    }
    
    public abstract void setPAPOrder(List<String> papIdList);
    
    public abstract void update(String papId, PAP newpap);

}
