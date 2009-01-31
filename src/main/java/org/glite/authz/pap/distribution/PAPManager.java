package org.glite.authz.pap.distribution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PAPDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.ui.wizard.BlacklistPolicySet;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicySet;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPManager {
    
    private static final Logger log = LoggerFactory.getLogger(PAPManager.class);
    private static PAPManager instance = null;
    protected static PAP localPAP = PAP.makeLocalPAP();
    
    public static PAPManager getInstance() {
        if (instance == null)
            instance = new PAPManager();
        return instance;
    }
    
    protected DistributionConfiguration distributionConfiguration;
    protected PAPDAO papDAO;
    protected List<PAP> papList;
    
    protected PAPManager() {
        distributionConfiguration = DistributionConfiguration.getInstance();
        papDAO = RepositoryManager.getDAOFactory().getPAPDAO();
        initPAPList();
    }
    
    public PAPContainer addTrustedPAP(PAP pap) {
        
        if (exists(pap.getPapId()))
            throw new AlreadyExistsException();
        
        distributionConfiguration.setPAPAndSave(pap);
        papList.add(pap);
        papDAO.store(pap);
        
        return new PAPContainer(pap);
    }
    
    public void createLocalPAPIfNotExists() {
        
        if (localPAPExists())
            return;
        
        papDAO.store(localPAP);
        
        PAPContainer localPAPContainer = getLocalPAPContainer();
        
        PolicySetType localPolicySet = PolicySetHelper.buildWithAnyTarget(localPAP.getPapId(),
                PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
        
        localPAPContainer.storePolicySet((new BlacklistPolicySet()).getPolicySetType());
        localPAPContainer.storePolicySet((new ServiceClassPolicySet()).getPolicySetType());
        
        PolicySetHelper.addPolicySetReference(localPolicySet, BlacklistPolicySet.POLICY_SET_ID);
        PolicySetHelper.addPolicySetReference(localPolicySet, ServiceClassPolicySet.POLICY_SET_ID);
        
        localPAPContainer.storePolicySet(localPolicySet);
    }
    
    public PAP deleteTrustedPAP(String papId) throws NotFoundException {
        PAP pap = getPAP(papId);
        distributionConfiguration.removePAP(pap.getAlias());
        papList.remove(pap);
        PAPContainer papContainer = new PAPContainer(pap);
        papContainer.erasePAP();
        return pap;
    }
    
    public boolean exists(String papId) {
        for (PAP pap:papList) {
            if (pap.getPapId().equals(papId))
                return true;
        }
        return false;
    }
    
    public List<PAP> getAllTrustedPAPs() {
        return new ArrayList<PAP>(papList);
    }
    
    public PAP getLocalPAP() {
        return localPAP;
    }
    
    public PAPContainer getLocalPAPContainer() {
        if (!localPAPExists())
            throw new NotFoundException("Critical error (probably a BUG): local PAP not found.");
        return new PAPContainer(localPAP);
    }
    
    public PAP getPAP(String papId) throws NotFoundException {
        for (PAP pap : papList) {
            if (pap.getPapId().equals(papId))
                return pap;
        }
        
        log.debug("Requested PAP not found:" + papId);
        throw new NotFoundException("PAP not found: " + papId);
    }
    
    public List<PAP> getPublicTrustedPAPs() {
        
        List<PAP> resultList = new LinkedList<PAP>();
        
        for (PAP pap:papList) {
            if (pap.isVisibilityPublic())
                resultList.add(pap);
        }
        
        return resultList;
    }
    
    public PAPContainer getTrustedPAPContainer(String papId) {
        return new PAPContainer(getPAP(papId));
    }
    
    public List<PAPContainer> getTrustedPAPContainerAll() {
        List<PAPContainer> papContainerList = new ArrayList<PAPContainer>(papList.size());
        for (PAP pap:papList) {
            papContainerList.add(new PAPContainer(pap));
        }
        return papContainerList;
    }
    
    public List<PAPContainer> getTrustedPAPContainerPublic() {
        List<PAPContainer> papContainerList = new LinkedList<PAPContainer>();
        for (PAP pap:papList) {
            if (pap.isVisibilityPublic())
                papContainerList.add(new PAPContainer(pap));
        }
        return papContainerList;
    }
    
    public void setTrustedPAPOrder(List<String> papIdList) {
        // TODO
    }
    
    public void updateTrustedPAP(String papId, PAP newpap) {
        
        boolean found = false;
        
        for (int i=0; i<papList.size(); i++) {
            PAP pap = papList.get(i);
            if (pap.getPapId().equals(papId)) {
                papList.set(i, newpap);
                found = true;
                break;
            }
        }
        
        if (!found)
            throw new NotFoundException("PAP not found (id=" + papId + ")");
    }
    
    private void initPAPList() {
        papList = DistributionConfiguration.getInstance().getRemotePAPList();
        
        // Add not already existing PAPs defined in the configuration file
        for (PAP pap:papList) {
            if (papDAO.papExistsByAlias(pap.getPapId()))
                continue;
            papDAO.store(pap);
        }
        
        // If the configuration was modified off-line 
        // remove unwanted PAPs still in the repository
        for (String papId:papDAO.getAllAliases()) {
            if (exists(papId))
                continue;
            papDAO.deleteByAlias(papId);
        }
    }
    
    private boolean localPAPExists() {
        return RepositoryManager.getDAOFactory().getPAPDAO().papExistsByAlias(localPAP.getPapId());
    }
    
}
