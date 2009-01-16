package org.glite.authz.pap.services;

import java.rmi.RemoteException;

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
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XACMLPolicyManagementService implements XACMLPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(XACMLPolicyManagementService.class);

    public PolicyType getPolicy(String policyId) throws RemoteException {
        log.info("getPolicy(" + policyId + ");");
        PolicyType policy = GetPolicyOperation.instance(policyId).execute();
        log.info("Policy found! Sending...");
        return policy;
    }

    public PolicySetType getPolicySet(String policySetId) throws RemoteException {
        log.info("getPolicySet(" + policySetId + ");");
        PolicySetType policySet = GetPolicySetOperation.instance(policySetId).execute();
        return policySet;
    }

    public boolean hasPolicy(String policyId) throws RemoteException {
        log.info("hasPolicy(" + policyId + ");");
        return HasPolicyOperation.instance(policyId).execute();
    }

    public boolean hasPolicySet(String policySetId) throws RemoteException {
        log.info("hasPolicySet(" + policySetId + ");");
        return HasPolicySetOperation.instance(policySetId).execute();
    }

    public PolicyType[] listPolicies() throws RemoteException {
        log.info("listPolicies();");
        PolicyType[] policyArray = ListPoliciesOperation.instance().execute();
        log.info("Returning " + policyArray.length + " policies");
        return policyArray;
    }

    public PolicyType[] listPAPPolicies(String papId) throws RemoteException {
        log.info("listPolicies(" + papId + ");");
        return ListPoliciesForPAPOperation.instance(papId).execute();
    }

    public PolicySetType[] listPolicySets() throws RemoteException {
        log.info("listPolicySets();");
        return ListPolicySetOperation.instance().execute();
    }

    public PolicySetType[] listPAPPolicySets(String papId) throws RemoteException {
        log.info("listPolicySets(" + papId + ");");
        return ListPolicySetsForPAPOperation.instance(papId).execute();
    }

    public boolean removePolicy(String policyId) throws RemoteException {
        log.info("removePolicy(" + policyId + ");");
        return RemovePolicyOperation.instance(policyId).execute();
    }

    public boolean removePolicySet(String policySetId) throws RemoteException {
        log.info("removePolicySet(" + policySetId + ");");
        return RemovePolicySetOperation.instance(policySetId).execute();
    }

    public String storePolicy(String idPrefix, PolicyType policy) throws RemoteException {
        log.info("storePolicy(" + idPrefix + "," + policy + ");");
        return StorePolicyOperation.instance(idPrefix, policy).execute();
    }

    public String storePolicySet(String idPrefix, PolicySetType policySet) throws RemoteException {
        log.info("storePolicySet(" + idPrefix + "," + policySet + ");");
        return StorePolicySetOperation.instance(idPrefix, policySet).execute();
    }

    public boolean updatePolicy(PolicyType policy) throws RemoteException {
        log.info("updatePolicy(" + policy + ");");
        return UpdatePolicyOperation.instance(policy).execute();
    }

    public boolean updatePolicySet(PolicySetType policySet) throws RemoteException {
        log.info("updatePolicySet(" + policySet + ");");
        return UpdatePolicySetOperation.instance(policySet).execute();
    }

}
