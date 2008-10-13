package org.glite.authz.pap.distribution;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PAPDAO;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicySetType;

public abstract class PAPManager {
    
    private static PAPManager instance = null;
    protected static PAP localPAP = PAP.makeLocalPAP();
    protected PAPDAO papDAO; 
    
    public static PAPManager getInstance() {
        if (instance == null)
            instance = new PAPManagerImpl();
        return instance;
    }
    
    protected List<PAP> papList;
    protected DistributionConfiguration distributionConfiguration;
    
    protected PAPManager() {
        distributionConfiguration = DistributionConfiguration.getInstance();
        papList = distributionConfiguration.getRemotePAPList();
        papDAO = RepositoryManager.getDAOFactory().getPAPDAO();
    }

    public abstract PAPContainer add(PAP pap);
    
    public void createLocalPAPIfNotExists() {
        
        if (localPAPExists())
            return;
        
        papDAO.add(localPAP);
        
        PolicySetType localPolicySet = PolicySetHelper.buildWithAnyTarget(localPAP.getPapId(),
                PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
        
        getLocalPAPContainer().storePolicySet(localPolicySet);
    }
    
    public abstract PAP delete(String papId) throws NotFoundException;
    
    public abstract boolean exists(String papId);

    public abstract PAP get(String papId) throws NotFoundException;

    public abstract List<PAP> getAll();

    public abstract PAPContainer getContainer(String papId);

    public abstract List<PAPContainer> getContainerAll();
    
    public PAP getLocalPAP() {
        return localPAP;
    }
    
    public PAPContainer getLocalPAPContainer() {
        if (!localPAPExists())
            throw new NotFoundException("Critical error (probably a BUG): local PAP not found.");
        return new PAPContainer(localPAP);
    }
    
    private boolean localPAPExists() {
        return RepositoryManager.getDAOFactory().getPAPDAO().exists(localPAP.getPapId());
    }
    
    public abstract void setPAPOrder(List<String> papIdList);
    
    public abstract void update(String papId, PAP newpap);

}
