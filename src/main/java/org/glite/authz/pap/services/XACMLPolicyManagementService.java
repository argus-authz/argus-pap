package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.operations.policymanagement.AddPoliciesOperation;
import org.glite.authz.pap.authz.operations.policymanagement.AddPolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.AddPolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.GetLocalPolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.GetLocalPolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.GetLocalRootPolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.GetRemotePolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.GetRemotePolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.GetRemoteRootPolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.HasLocalPolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.HasLocalPolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.HasRemotePolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.HasRemotePolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.ListLocalPoliciesOperation;
import org.glite.authz.pap.authz.operations.policymanagement.ListLocalPolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.ListRemotePoliciesOperation;
import org.glite.authz.pap.authz.operations.policymanagement.ListRemotePolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.MoveOperation;
import org.glite.authz.pap.authz.operations.policymanagement.RemoveObjectByIdAndReferencesOperation;
import org.glite.authz.pap.authz.operations.policymanagement.RemovePolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.RemovePolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.StorePolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.StorePolicySetOperation;
import org.glite.authz.pap.authz.operations.policymanagement.UpdatePolicyOperation;
import org.glite.authz.pap.authz.operations.policymanagement.UpdatePolicySetOperation;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;
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

            ServicesUtils.beginTransaction();

            String[] result = AddPoliciesOperation.instance(alias,
                                                            index,
                                                            policySetId,
                                                            policyIdPrefixArray,
                                                            policyArray).execute();
            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public String addPolicy(String alias, int index, String policySetId, String policyIdPrefix,
            PolicyType policy) throws RemoteException {

        log.info(String.format("addPolicy(policySetId=\"%s\", policyIdPrefix=\"%s\");",
                               policySetId,
                               policyIdPrefix));
        try {

            ServicesUtils.beginTransaction();

            String result = AddPolicyOperation.instance(alias, index, policySetId, policyIdPrefix, policy)
                                              .execute();
            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public String addPolicySet(String alias, int index, PolicySetType policySet) throws RemoteException {

        log.info(String.format("addPolicySet(policySetId=\"%s\");", policySet.getPolicySetId()));

        try {

            ServicesUtils.beginTransaction();

            String result = AddPolicySetOperation.instance(alias, index, policySet).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public PolicySetType getRootPolicySet(String alias) throws RemoteException {
        log.info(String.format("getPAPPolicySet(\"%s\");", alias));

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            ServicesUtils.beginTransaction();

            Pap ps = PapManager.getInstance().getPap(alias);

            PolicySetType result;

            if (ps.isLocal()) {
                result = GetLocalRootPolicySetOperation.instance(ps).execute();
            } else {
                result = GetRemoteRootPolicySetOperation.instance(ps).execute();
            }

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public PolicyType getPolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("getPolicy(\"%s\");", policyId));

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            ServicesUtils.beginTransaction();

            Pap ps = PapManager.getInstance().getPap(alias);

            PolicyType policy;

            if (ps.isLocal()) {
                policy = GetLocalPolicyOperation.instance(ps, policyId).execute();
            } else {
                policy = GetRemotePolicyOperation.instance(ps, policyId).execute();
            }

            ServicesUtils.commitTransaction();

            return policy;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public PolicySetType getPolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("getPolicySet(\"%s\");", policySetId));

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            ServicesUtils.beginTransaction();

            Pap ps = PapManager.getInstance().getPap(alias);

            PolicySetType policySet;

            if (ps.isLocal()) {
                policySet = GetLocalPolicySetOperation.instance(ps, policySetId).execute();
            } else {
                policySet = GetRemotePolicySetOperation.instance(ps, policySetId).execute();
            }

            ServicesUtils.commitTransaction();

            return policySet;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean hasPolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("hasPolicy(\"%s\");", policyId));
        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            ServicesUtils.beginTransaction();

            Pap ps = PapManager.getInstance().getPap(alias);

            boolean result;

            if (ps.isLocal()) {
                result = HasLocalPolicyOperation.instance(ps, policyId).execute();
            } else {
                result = HasRemotePolicyOperation.instance(ps, policyId).execute();
            }

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean hasPolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("hasPolicySet(\"%s\");", policySetId));
        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            ServicesUtils.beginTransaction();

            Pap ps = PapManager.getInstance().getPap(alias);

            boolean result;

            if (ps.isLocal()) {
                result = HasLocalPolicySetOperation.instance(ps, policySetId).execute();
            } else {
                result = HasRemotePolicySetOperation.instance(ps, policySetId).execute();
            }

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public PolicyType[] listPolicies(String alias) throws RemoteException {
        log.info("listPolicies();");

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            ServicesUtils.beginTransaction();

            Pap ps = PapManager.getInstance().getPap(alias);

            PolicyType[] result;

            if (ps.isLocal()) {
                result = ListLocalPoliciesOperation.instance(ps).execute();
            } else {

                result = ListRemotePoliciesOperation.instance(ps).execute();
            }

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public PolicySetType[] listPolicySets(String alias) throws RemoteException {
        log.info("listPolicySets();");

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            ServicesUtils.beginTransaction();

            Pap ps = PapManager.getInstance().getPap(alias);

            PolicySetType[] result;

            if (ps.isLocal()) {
                result = ListLocalPolicySetOperation.instance(ps).execute();
            } else {
                result = ListRemotePolicySetOperation.instance(ps).execute();
            }

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public void move(String alias, String id, String pivotId, boolean moveAfter) throws RemoteException {
        log.info(String.format("move(id=\"%s\", pivotId=\"%s\", moveAfter=%b);", id, pivotId, moveAfter));

        try {

            ServicesUtils.beginTransaction();

            MoveOperation.instance(alias, id, pivotId, moveAfter).execute();

            ServicesUtils.commitTransaction();

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean removeObjectByIdAndReferences(String alias, String id) throws RemoteException {
        log.info(String.format("removeObjectByIdAndReferences(\"%s\");", id));

        try {

            ServicesUtils.beginTransaction();

            boolean result = RemoveObjectByIdAndReferencesOperation.instance(alias, id).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean removePolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("removePolicy(\"%s\");", policyId));
        try {

            ServicesUtils.beginTransaction();

            boolean result = RemovePolicyOperation.instance(alias, policyId).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean removePolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("removePolicySet(\"%s\");", policySetId));

        try {

            ServicesUtils.beginTransaction();

            boolean result = RemovePolicySetOperation.instance(alias, policySetId).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public String storePolicy(String alias, String idPrefix, PolicyType policy) throws RemoteException {
        log.info("storePolicy();");

        try {

            ServicesUtils.beginTransaction();

            String result = StorePolicyOperation.instance(alias, idPrefix, policy).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public String storePolicySet(String alias, String idPrefix, PolicySetType policySet)
            throws RemoteException {
        log.info("storePolicySet();");

        try {

            ServicesUtils.beginTransaction();

            String result = StorePolicySetOperation.instance(alias, idPrefix, policySet).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean updatePolicy(String alias, String version, PolicyType policy) throws RemoteException {
        log.info(String.format("updatePolicy(version=\"%s\", id=\"%s\");", version, policy.getPolicyId()));

        try {

            ServicesUtils.beginTransaction();

            boolean result = UpdatePolicyOperation.instance(alias, version, policy).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }

    public boolean updatePolicySet(String alias, String version, PolicySetType policySet)
            throws RemoteException {
        log.info(String.format("updatePolicySet(version=\"%s\", id=\"%s\");",
                               version,
                               policySet.getPolicySetId()));

        try {

            ServicesUtils.beginTransaction();

            boolean result = UpdatePolicySetOperation.instance(alias, version, policySet).execute();

            ServicesUtils.commitTransaction();

            return result;

        } catch (RuntimeException e) {
            ServicesExceptionManager.logAndRollback(log, e);
            throw e;
        }
    }
}
