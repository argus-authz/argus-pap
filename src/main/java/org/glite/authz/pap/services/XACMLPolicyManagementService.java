package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.policymanagement.AddPoliciesOperation;
import org.glite.authz.pap.authz.policymanagement.AddPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.AddPolicySetOperation;
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
import org.glite.authz.pap.authz.policymanagement.MoveOperation;
import org.glite.authz.pap.authz.policymanagement.RemoveObjectByIdAndReferencesOperation;
import org.glite.authz.pap.authz.policymanagement.RemovePolicyOperation;
import org.glite.authz.pap.authz.policymanagement.RemovePolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.StorePolicyOperation;
import org.glite.authz.pap.authz.policymanagement.StorePolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.UpdatePolicyOperation;
import org.glite.authz.pap.authz.policymanagement.UpdatePolicySetOperation;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XACMLPolicyManagementService implements XACMLPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(XACMLPolicyManagementService.class);

    public String[] addPolicies(int index, String policySetId, String[] policyIdPrefixArray, PolicyType[] policyArray)
            throws RemoteException {
        log.info(String.format("addPolicy(policySetId=\"%s\"\");", policySetId));

        try {

            synchronized (PAPContainer.addOperationLock) {

                return AddPoliciesOperation.instance(index, policySetId, policyIdPrefixArray, policyArray).execute();

            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String addPolicy(int index, String policySetId, String policyIdPrefix, PolicyType policy) throws RemoteException {

        log.info(String.format("addPolicy(policySetId=\"%s\", policyIdPrefix=\"%s\");", policySetId, policyIdPrefix));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return AddPolicyOperation.instance(index, policySetId, policyIdPrefix, policy).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String addPolicySet(int index, PolicySetType policySet) throws RemoteException {

        log.info(String.format("addPolicySet(policySetId=\"%s\");", policySet.getPolicySetId()));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return AddPolicySetOperation.instance(index, policySet).execute();
            }

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

            synchronized (PAPContainer.addOperationLock) {
                return ListPoliciesOperation.instance().execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType[] listPolicySets() throws RemoteException {
        log.info("listPolicySets();");

        try {

            synchronized (PAPContainer.addOperationLock) {
                return ListPolicySetOperation.instance().execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public void move(String id, String pivotId, boolean moveAfter) throws RemoteException {
        log.info(String.format("move(id=\"%s\", pivotId=\"%s\", moveAfter=%b);", id, pivotId, moveAfter));

        try {

            synchronized (PAPContainer.addOperationLock) {
                MoveOperation.instance(id, pivotId, moveAfter).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removeObjectByIdAndReferences(String id) throws RemoteException {
        log.info(String.format("removeObjectByIdAndReferences(\"%s\");", id));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return RemoveObjectByIdAndReferencesOperation.instance(id).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePolicy(String policyId) throws RemoteException {
        log.info(String.format("removePolicy(\"%s\");", policyId));
        try {

            synchronized (PAPContainer.addOperationLock) {
                return RemovePolicyOperation.instance(policyId).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePolicySet(String policySetId) throws RemoteException {
        log.info(String.format("removePolicySet(\"%s\");", policySetId));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return RemovePolicySetOperation.instance(policySetId).execute();
            }

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

    public boolean updatePolicy(String version, PolicyType policy) throws RemoteException {
        log.info(String.format("updatePolicy(version=\"%s\", id=\"%s\");", version, policy.getPolicyId()));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return UpdatePolicyOperation.instance(version, policy).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePolicySet(String version, PolicySetType policySet) throws RemoteException {
        log.info(String.format("updatePolicySet(version=\"%s\", id=\"%s\");", version, policySet.getPolicySetId()));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return UpdatePolicySetOperation.instance(version, policySet).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }
}
