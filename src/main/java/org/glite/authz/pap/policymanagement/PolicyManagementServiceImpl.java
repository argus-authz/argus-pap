package org.glite.authz.pap.policymanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyManagementServiceImpl implements PolicyManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(PolicyManagementServiceImpl.class);
    
    private static PAPContainer getLocalPAPContainer() {
        return PAPManager.getInstance().getLocalPAPContainer();
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#getPolicy(java.lang.String)
     */
    public PolicyType getPolicy(String policyId) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        if (!localPAP.hasPolicy(policyId))
            throw new NotFoundException("Policy '" + policyId + "' not found.");
        
        PolicyType policy = localPAP.getPolicy(policyId);
        
        log.debug("Returning policy:\n" + XMLObjectHelper.toString(policy));
        
        return policy;
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#getPolicySet(java.lang.String)
     */
    public PolicySetType getPolicySet(String policySetId) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        if (!localPAP.hasPolicySet(policySetId))
            throw new NotFoundException("PolicySet '" + policySetId + "' not found.");
        
        PolicySetType policySet = localPAP.getPolicySet(policySetId);
        
        log.debug("Returning PolicySet:\n" + XMLObjectHelper.toString(policySet));
        
        return policySet;
    }

    /* (non-Javadoc)
    * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies()
    */
    public boolean hasPolicy(String policyId) throws RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        return localPAP.hasPolicy(policyId);
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies()
     */
    public boolean hasPolicySet(String policySetId) throws RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        return localPAP.hasPolicySet(policySetId);
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies()
     */
    public List<PolicyType> listPolicies() throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        return localPAP.getAllPolicies();
        
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies(java.lang.String)
     */
    public List<PolicyType> listPolicies(String papId) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer pap = PAPManager.getInstance().getContainer(papId);
        
        return pap.getAllPolicies();
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicySets()
     */
    public List<PolicySetType> listPolicySets() throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        return localPAP.getAllPolicySets();
    }
    
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicySets(java.lang.String)
     */
    public List<PolicySetType> listPolicySets(String papId) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer pap = PAPManager.getInstance().getContainer(papId);
        
        return pap.getAllPolicySets();
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#removePolicy(java.lang.String)
     */
    public void removePolicy(String policyId) throws NotFoundException, RepositoryException, java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        localPAP.deletePolicy(policyId);
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#removePolicySet(java.lang.String)
     */
    public void removePolicySet(String policySetId) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        localPAP.deletePolicySet(policySetId);
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#storePolicy(org.opensaml.xacml.policy.PolicyType)
     */
    public String storePolicy(String idPrefix, PolicyType policy) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        String policyId = PolicyWizard.generateId(idPrefix);
        policy.setPolicyId(policyId);
        
        localPAP.storePolicy(policy);
        
        return policyId;
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#storePolicySet(org.opensaml.xacml.policy.PolicySetType)
     */
    public String storePolicySet(String idPrefix, PolicySetType policySet) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        String policySetId = policySet.getPolicySetId();
        
        localPAP.storePolicySet(policySet);
        
        return policySetId;
    }

    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#updatePolicy(org.opensaml.xacml.policy.PolicyType)
     */
    public void updatePolicy(PolicyType policy) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        localPAP.storePolicy(policy);
    }

    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#updatePolicySet(org.opensaml.xacml.policy.PolicySetType)
     */
    public void updatePolicySet(PolicySetType policySet) throws java.rmi.RemoteException {
        log.debug("Received request");
        
        PAPContainer localPAP = getLocalPAPContainer();
        
        localPAP.storePolicySet(policySet);
    }
    
}
