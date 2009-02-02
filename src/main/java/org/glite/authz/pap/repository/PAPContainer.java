package org.glite.authz.pap.repository;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPContainer {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(PAPContainer.class);

    private final PAP pap;
    private final String papId;
    private final PolicySetDAO policySetDAO;
    private final PolicyDAO policyDAO;

    public PAPContainer(PAP pap) {
        this.pap = pap;
        papId = pap.getPapId();
        policySetDAO = DAOFactory.getDAOFactory().getPolicySetDAO();
        policyDAO = DAOFactory.getDAOFactory().getPolicyDAO();
    }

    public void deleteAllPolicies() {
        policyDAO.deleteAll(papId);
    }

    public void deleteAllPolicySets() {
        policySetDAO.deleteAll(papId);
    }

    public void deletePolicy(String id) throws NotFoundException, RepositoryException {
        policyDAO.delete(papId, id);
    }

    public void deletePolicySet(String id) throws NotFoundException, RepositoryException {
        policySetDAO.delete(papId, id);
    }

    public List<PolicyType> getAllPolicies() {
        return policyDAO.getAll(papId);
    }
    
    public List<PolicySetType> getAllPolicySets() {
        List<PolicySetType> policySetList = policySetDAO.getAll(papId);
        
        // Place the PAP root PolicySet as the first element
        for (PolicySetType policySetElement : policySetList) {
            if (policySetElement.getPolicySetId().equals(papId)) {
                
                int currentIndex = policySetList.indexOf(policySetElement);
                
                if (currentIndex != 0) { // swap elements
                    PolicySetType tempPolicySet = policySetList.get(0);
                    policySetList.set(0, policySetElement);
                    policySetList.set(currentIndex, tempPolicySet);
                }
            }
        }
        return policySetDAO.getAll(papId);
    }

    public PAP getPAP() {
        return this.pap;
    }

    public PolicySetType getPAPRootPolicySet()
    {
        return policySetDAO.getById(papId, getPAPRootPolicySetId());
    }
    
    public String getPAPRootPolicySetId()
    {
        return papId;
    }

    public PolicyType getPolicy(String id) {
        return policyDAO.getById(papId, id);
    }
    
    public PolicySetType getPolicySet(String id) {
        return policySetDAO.getById(papId, id);
    }

    public boolean hasPolicy(String id) {
        return policyDAO.exists(papId, id);
    }

    public boolean hasPolicySet(String id) {
        return policySetDAO.exists(papId, id);
    }

    public void storePolicy(PolicyType policy) {
        
        if (policyDAO.exists(papId, policy.getPolicyId()))
            policyDAO.update(papId, policy);
        else
            policyDAO.store(papId, policy);

    }

    public void storePolicySet(PolicySetType policySet) {
        
        if (policySetDAO.exists(papId, policySet.getPolicySetId()))
            policySetDAO.update(papId, policySet);
        else
            policySetDAO.store(papId, policySet);

    }
}
