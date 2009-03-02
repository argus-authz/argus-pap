package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.policymanagement.AddPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.GetPAPPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.GetPAPPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.GetPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.GetPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.HasPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.HasPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.ListPoliciesForPAPOperation;
import org.glite.authz.pap.authz.policymanagement.ListPoliciesOperation;
import org.glite.authz.pap.authz.policymanagement.ListPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.ListPolicySetsForPAPOperation;
import org.glite.authz.pap.authz.policymanagement.RemovePolicyAndReferencesOperation;
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

    public String addPolicy(String policySetId, String policyIdPrefix, PolicyType policy) throws RemoteException {

        log.info(String.format("addPolicy(policySetId=\"%s\", policyIdPrefix=\"%s\");", policySetId, policyIdPrefix));

        try {

            return AddPolicyOperation.instance(policySetId, policyIdPrefix, policy).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }

    }

    public PolicyType getPAPPolicy(String papAlias, String policyId) throws RemoteException {
        log.info(String.format("getPAPPolicy(\"%s\", \"%s\");", papAlias, policyId));

        try {

            return GetPAPPolicyOperation.instance(papAlias, policyId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType getPAPPolicySet(String papAlias, String policySetId) throws RemoteException {
        log.info(String.format("getPAPPolicySet(\"%s\", \"%s\");", papAlias, policySetId));

        try {

            return GetPAPPolicySetOperation.instance(papAlias, policySetId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicyType getPolicy(String policyId) throws RemoteException {
        log.info(String.format("getPolicy(\"%s\");", policyId));

        try {

            PolicyType policy = GetPolicyOperation.instance(policyId).execute();
            return policy;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType getPolicySet(String policySetId) throws RemoteException {
        log.info(String.format("getPolicySet(\"%s\");", policySetId));

        try {

            PolicySetType policySet = GetPolicySetOperation.instance(policySetId).execute();
            return policySet;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean hasPolicy(String policyId) throws RemoteException {
        log.info(String.format("hasPolicy(\"%s\");", policyId));
        try {

            return HasPolicyOperation.instance(policyId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean hasPolicySet(String policySetId) throws RemoteException {
        log.info(String.format("hasPolicySet(\"%s\");", policySetId));
        try {

            return HasPolicySetOperation.instance(policySetId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicyType[] listPAPPolicies(String papAlias) throws RemoteException {
        log.info(String.format("lisPAPPolicies(\"%s\");", papAlias));
        try {

            return ListPoliciesForPAPOperation.instance(papAlias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType[] listPAPPolicySets(String papAlias) throws RemoteException {
        log.info(String.format("listPolicySets(\"%s\");", papAlias));

        try {

            return ListPolicySetsForPAPOperation.instance(papAlias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicyType[] listPolicies() throws RemoteException {
        log.info("listPolicies();");

        try {

            return ListPoliciesOperation.instance().execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType[] listPolicySets() throws RemoteException {
        log.info("listPolicySets();");

        try {

            return ListPolicySetOperation.instance().execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePolicy(String policyId) throws RemoteException {
        log.info(String.format("removePolicy(\"%s\");", policyId));
        try {

            return RemovePolicyOperation.instance(policyId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePolicyAndReferences(String policyId) throws RemoteException {
        log.info(String.format("removePolicyAndReferences(\"%s\");", policyId));

        try {

            return RemovePolicyAndReferencesOperation.instance(policyId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePolicySet(String policySetId) throws RemoteException {
        log.info(String.format("removePolicySet(\"%s\");", policySetId));

        try {

            return RemovePolicySetOperation.instance(policySetId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String storePolicy(String idPrefix, PolicyType policy) throws RemoteException {
        log.info("storePolicy();");
        
        try {

            return StorePolicyOperation.instance(idPrefix, policy).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String storePolicySet(String idPrefix, PolicySetType policySet) throws RemoteException {
        log.info("storePolicySet();");
        
        try {

            return StorePolicySetOperation.instance(idPrefix, policySet).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePolicy(PolicyType policy) throws RemoteException {
        log.info("updatePolicy();");
        
        try {

            return UpdatePolicyOperation.instance(policy).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePolicySet(PolicySetType policySet) throws RemoteException {
        log.info("updatePolicySet();");
        
        try {

            return UpdatePolicySetOperation.instance(policySet).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

}
