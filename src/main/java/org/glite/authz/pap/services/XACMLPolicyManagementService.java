package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.policymanagement.AddPoliciesOperation;
import org.glite.authz.pap.authz.policymanagement.AddPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.AddPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.GetRootPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.GetPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.GetPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.HasPolicyOperation;
import org.glite.authz.pap.authz.policymanagement.HasPolicySetOperation;
import org.glite.authz.pap.authz.policymanagement.ListPoliciesOperation;
import org.glite.authz.pap.authz.policymanagement.ListPolicySetOperation;
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

    public String[] addPolicies(String alias, int index, String policySetId, String[] policyIdPrefixArray,
            PolicyType[] policyArray) throws RemoteException {
        log.info(String.format("addPolicy(policySetId=\"%s\"\");", policySetId));

        try {

            synchronized (PAPContainer.addOperationLock) {

                return AddPoliciesOperation.instance(alias, index, policySetId, policyIdPrefixArray, policyArray).execute();

            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String addPolicy(String alias, int index, String policySetId, String policyIdPrefix, PolicyType policy)
            throws RemoteException {

        log.info(String.format("addPolicy(policySetId=\"%s\", policyIdPrefix=\"%s\");", policySetId, policyIdPrefix));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return AddPolicyOperation.instance(alias, index, policySetId, policyIdPrefix, policy).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String addPolicySet(String alias, int index, PolicySetType policySet) throws RemoteException {

        log.info(String.format("addPolicySet(policySetId=\"%s\");", policySet.getPolicySetId()));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return AddPolicySetOperation.instance(alias, index, policySet).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType getRootPolicySet(String papAlias) throws RemoteException {
        log.info(String.format("getPAPPolicySet(\"%s\");", papAlias));

        try {

            return GetRootPolicySetOperation.instance(papAlias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicyType getPolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("getPolicy(\"%s\");", policyId));

        try {

            PolicyType policy = GetPolicyOperation.instance(alias, policyId).execute();
            return policy;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType getPolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("getPolicySet(\"%s\");", policySetId));

        try {

            PolicySetType policySet = GetPolicySetOperation.instance(alias, policySetId).execute();
            return policySet;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean hasPolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("hasPolicy(\"%s\");", policyId));
        try {

            return HasPolicyOperation.instance(alias, policyId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean hasPolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("hasPolicySet(\"%s\");", policySetId));
        try {

            return HasPolicySetOperation.instance(alias, policySetId).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicyType[] listPolicies(String alias) throws RemoteException {
        log.info("listPolicies();");

        try {

            synchronized (PAPContainer.addOperationLock) {
                return ListPoliciesOperation.instance(alias).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType[] listPolicySets(String alias) throws RemoteException {
        log.info("listPolicySets();");

        try {

            synchronized (PAPContainer.addOperationLock) {
                return ListPolicySetOperation.instance(alias).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public void move(String alias, String id, String pivotId, boolean moveAfter) throws RemoteException {
        log.info(String.format("move(id=\"%s\", pivotId=\"%s\", moveAfter=%b);", id, pivotId, moveAfter));

        try {

            synchronized (PAPContainer.addOperationLock) {
                MoveOperation.instance(alias, id, pivotId, moveAfter).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removeObjectByIdAndReferences(String alias, String id) throws RemoteException {
        log.info(String.format("removeObjectByIdAndReferences(\"%s\");", id));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return RemoveObjectByIdAndReferencesOperation.instance(alias, id).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("removePolicy(\"%s\");", policyId));
        try {

            synchronized (PAPContainer.addOperationLock) {
                return RemovePolicyOperation.instance(alias, policyId).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean removePolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("removePolicySet(\"%s\");", policySetId));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return RemovePolicySetOperation.instance(alias, policySetId).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String storePolicy(String alias, String idPrefix, PolicyType policy) throws RemoteException {
        log.info("storePolicy();");

        try {

            return StorePolicyOperation.instance(alias, idPrefix, policy).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String storePolicySet(String alias, String idPrefix, PolicySetType policySet) throws RemoteException {
        log.info("storePolicySet();");

        try {

            return StorePolicySetOperation.instance(alias, idPrefix, policySet).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePolicy(String alias, String version, PolicyType policy) throws RemoteException {
        log.info(String.format("updatePolicy(version=\"%s\", id=\"%s\");", version, policy.getPolicyId()));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return UpdatePolicyOperation.instance(alias, version, policy).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePolicySet(String alias, String version, PolicySetType policySet) throws RemoteException {
        log.info(String.format("updatePolicySet(version=\"%s\", id=\"%s\");", version, policySet.getPolicySetId()));

        try {

            synchronized (PAPContainer.addOperationLock) {
                return UpdatePolicySetOperation.instance(alias, version, policySet).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }
}
