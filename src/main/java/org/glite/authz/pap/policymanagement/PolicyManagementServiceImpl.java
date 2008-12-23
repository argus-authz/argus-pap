package org.glite.authz.pap.policymanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.glite.authz.pap.authz.policymanagement.GetPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.GetPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.HasPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.HasPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.ListPoliciesForPAPOperation;
import org.glite.authz.pap.authz.policymanagement.ListPoliciesOperation;
import org.glite.authz.pap.authz.policymanagement.ListPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.ListPolicySetsForPAPOperation;
import org.glite.authz.pap.authz.policymanagement.RemovePolicyOperation;
import org.glite.authz.pap.authz.policymanagement.RemovePolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.StorePolicyOperation;
import org.glite.authz.pap.authz.policymanagement.StorePolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.UpdatePolicyOperation;
import org.glite.authz.pap.authz.policymanagement.UpdatePolicySetOperation;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyManagementServiceImpl implements PolicyManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(PolicyManagementServiceImpl.class);
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#getPolicy(java.lang.String)
     */
    public PolicyType getPolicy(String policyId) throws java.rmi.RemoteException {
        
        log.info( "getPolicy("+policyId+ ");" );
        return GetPolicyOperation.instance( policyId ).execute();
        
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#getPolicySet(java.lang.String)
     */
    public PolicySetType getPolicySet(String policySetId) throws java.rmi.RemoteException {
        log.info( "getPolicySet("
                + policySetId + ");" );
        
        return GetPolicySetOperation.instance( policySetId ).execute();        
        
    }

    /* (non-Javadoc)
    * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies()
    */
    public boolean hasPolicy(String policyId) throws RemoteException {
        log.info( "hasPolicy(" + policyId + ");" );
        
        return HasPolicyOperation.instance( policyId ).execute(); 
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies()
     */
    public boolean hasPolicySet(String policySetId) throws RemoteException {
        
        log.info( "hasPolicySet(" + policySetId + ");" );
        
        return HasPolicySetOperation.instance( policySetId ).execute();
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies()
     */
    public List<PolicyType> listPolicies() throws java.rmi.RemoteException {
        log.info( "listPolicies();" );
                
        return ListPoliciesOperation.instance().execute();
        
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicies(java.lang.String)
     */
    public List<PolicyType> listPolicies(String papId) throws java.rmi.RemoteException {
        log.info( "listPolicies(" + papId + ");" );
        
        return ListPoliciesForPAPOperation.instance( papId ).execute();
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicySets()
     */
    public List<PolicySetType> listPolicySets() throws java.rmi.RemoteException {
        
        log.info( "listPolicySets();" );
        
        return ListPolicySetOperation.instance().execute();
        
    }
    
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#listPolicySets(java.lang.String)
     */
    public List<PolicySetType> listPolicySets(String papId) throws java.rmi.RemoteException {
        log.info( "listPolicySets(" + papId + ");" );
        
        return ListPolicySetsForPAPOperation.instance( papId ).execute();
        
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#removePolicy(java.lang.String)
     */
    public void removePolicy(String policyId) throws NotFoundException, RepositoryException, java.rmi.RemoteException {
        
        log.info( "removePolicy(" + policyId + ");" );
        RemovePolicyOperation.instance( policyId ).execute();
        
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#removePolicySet(java.lang.String)
     */
    public void removePolicySet(String policySetId) throws java.rmi.RemoteException {
        log.info( "removePolicySet(" + policySetId + ");" );
        
        RemovePolicySetOperation.instance( policySetId ).execute();
        
    }
    
    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#storePolicy(org.opensaml.xacml.policy.PolicyType)
     */
    public String storePolicy(String idPrefix, PolicyType policy) throws java.rmi.RemoteException {
        log.info( "storePolicy(" + idPrefix+","+ policy + ");" );
        
        return StorePolicyOperation.instance( idPrefix, policy ).execute();
        
    }

    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#storePolicySet(org.opensaml.xacml.policy.PolicySetType)
     */
    public String storePolicySet(String idPrefix, PolicySetType policySet) throws java.rmi.RemoteException {
        
        log.info( "storePolicySet(" + idPrefix+","+ policySet + ");" );
        
        return StorePolicySetOperation.instance( idPrefix, policySet ).execute();
        
    }

    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#updatePolicy(org.opensaml.xacml.policy.PolicyType)
     */
    public void updatePolicy(PolicyType policy) throws java.rmi.RemoteException {
        log.info( "updatePolicy(" + policy + ");" );
        
        UpdatePolicyOperation.instance( policy ).execute();
        
    }

    /* (non-Javadoc)
     * @see org.glite.authz.pap.policymanagement.PolicyManagementService#updatePolicySet(org.opensaml.xacml.policy.PolicySetType)
     */
    public void updatePolicySet(PolicySetType policySet) throws java.rmi.RemoteException {
        log.info( "updatePolicySet(" + policySet + ");" );
        
        UpdatePolicySetOperation.instance( policySet ).execute();
    }
    
}
