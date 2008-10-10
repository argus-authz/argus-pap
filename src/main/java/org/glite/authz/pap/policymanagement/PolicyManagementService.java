package org.glite.authz.pap.policymanagement;

import java.util.List;

import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public interface PolicyManagementService {

    public abstract PolicyType getPolicy(String policyId) throws java.rmi.RemoteException;

    public abstract PolicySetType getPolicySet(String policySetId) throws java.rmi.RemoteException;
    
    public abstract boolean hasPolicy(String policyId) throws java.rmi.RemoteException;
    
    public abstract boolean hasPolicySet(String policySetId) throws java.rmi.RemoteException;

    public abstract List<PolicyType> listPolicies() throws java.rmi.RemoteException;

    public abstract List<PolicyType> listPolicies(String papId) throws java.rmi.RemoteException;

    public abstract List<PolicySetType> listPolicySets() throws java.rmi.RemoteException;

    public abstract List<PolicySetType> listPolicySets(String papId) throws java.rmi.RemoteException;

    public abstract void removePolicy(String policyId) throws NotFoundException, RepositoryException,
            java.rmi.RemoteException;

    public abstract void removePolicySet(String policySetId) throws java.rmi.RemoteException;

    public abstract String storePolicy(String idPrefix, PolicyType policy)
            throws java.rmi.RemoteException;

    public abstract String storePolicySet(String idPrefix, PolicySetType policySet)
            throws java.rmi.RemoteException;

    public abstract void updatePolicy(PolicyType policy) throws java.rmi.RemoteException;

    public abstract void updatePolicySet(PolicySetType policySet) throws java.rmi.RemoteException;

}