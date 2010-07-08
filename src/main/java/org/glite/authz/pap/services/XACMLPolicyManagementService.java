/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            synchronized (ServicesUtils.highLevelOperationLock) {

                return AddPoliciesOperation.instance(alias,
                                                     index,
                                                     policySetId,
                                                     policyIdPrefixArray,
                                                     policyArray).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String addPolicy(String alias, int index, String policySetId, String policyIdPrefix,
            PolicyType policy) throws RemoteException {

        log.info(String.format("addPolicy(policySetId=\"%s\", policyIdPrefix=\"%s\");",
                               policySetId,
                               policyIdPrefix));
        try {

            synchronized (ServicesUtils.highLevelOperationLock) {
                return AddPolicyOperation.instance(alias, index, policySetId, policyIdPrefix, policy)
                                         .execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String addPolicySet(String alias, int index, PolicySetType policySet) throws RemoteException {

        log.info(String.format("addPolicySet(policySetId=\"%s\");", policySet.getPolicySetId()));

        try {
            synchronized (ServicesUtils.highLevelOperationLock) {
                return AddPolicySetOperation.instance(alias, index, policySet).execute();
            }
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType getRootPolicySet(String alias) throws RemoteException {
        log.info(String.format("getPAPPolicySet(\"%s\");", alias));

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            Pap ps = PapManager.getInstance().getPap(alias);

            if (ps.isLocal()) {
                return GetLocalRootPolicySetOperation.instance(ps).execute();
            } else {
                return GetRemoteRootPolicySetOperation.instance(ps).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicyType getPolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("getPolicy(\"%s\");", policyId));

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            Pap ps = PapManager.getInstance().getPap(alias);

            PolicyType policy;

            if (ps.isLocal()) {
                policy = GetLocalPolicyOperation.instance(ps, policyId).execute();
            } else {
                policy = GetRemotePolicyOperation.instance(ps, policyId).execute();
            }

            return policy;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType getPolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("getPolicySet(\"%s\");", policySetId));

        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            Pap ps = PapManager.getInstance().getPap(alias);

            PolicySetType policySet;

            if (ps.isLocal()) {
                policySet = GetLocalPolicySetOperation.instance(ps, policySetId).execute();
            } else {
                policySet = GetRemotePolicySetOperation.instance(ps, policySetId).execute();
            }
            return policySet;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean hasPolicy(String alias, String policyId) throws RemoteException {
        log.info(String.format("hasPolicy(\"%s\");", policyId));
        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            Pap ps = PapManager.getInstance().getPap(alias);

            synchronized (ServicesUtils.highLevelOperationLock) {
                if (ps.isLocal()) {
                    return HasLocalPolicyOperation.instance(ps, policyId).execute();
                } else {
                    return HasRemotePolicyOperation.instance(ps, policyId).execute();
                }
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean hasPolicySet(String alias, String policySetId) throws RemoteException {
        log.info(String.format("hasPolicySet(\"%s\");", policySetId));
        try {

            if (alias == null) {
                alias = Pap.DEFAULT_PAP_ALIAS;
            }

            Pap ps = PapManager.getInstance().getPap(alias);

            synchronized (ServicesUtils.highLevelOperationLock) {
                if (ps.isLocal()) {
                    return HasLocalPolicySetOperation.instance(ps, policySetId).execute();
                } else {
                    return HasRemotePolicySetOperation.instance(ps, policySetId).execute();
                }
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicyType[] listPolicies(String papAlias) throws RemoteException {
        log.info("listPolicies();");

        try {

            if (papAlias == null) {
                papAlias = Pap.DEFAULT_PAP_ALIAS;
            }

            Pap ps = PapManager.getInstance().getPap(papAlias);

            synchronized (ServicesUtils.highLevelOperationLock) {
                if (ps.isLocal()) {
                    return ListLocalPoliciesOperation.instance(ps).execute();
                } else {
                    return ListRemotePoliciesOperation.instance(ps).execute();
                }
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public PolicySetType[] listPolicySets(String papAlias) throws RemoteException {
        log.info("listPolicySets();");

        try {

            if (papAlias == null) {
                papAlias = Pap.DEFAULT_PAP_ALIAS;
            }

            Pap ps = PapManager.getInstance().getPap(papAlias);

            synchronized (ServicesUtils.highLevelOperationLock) {
                if (ps.isLocal()) {
                    return ListLocalPolicySetOperation.instance(ps).execute();
                } else {
                    return ListRemotePolicySetOperation.instance(ps).execute();
                }
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public void move(String alias, String id, String pivotId, boolean moveAfter) throws RemoteException {
        log.info(String.format("move(id=\"%s\", pivotId=\"%s\", moveAfter=%b);", id, pivotId, moveAfter));

        try {

            synchronized (ServicesUtils.highLevelOperationLock) {
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

            synchronized (ServicesUtils.highLevelOperationLock) {
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

            synchronized (ServicesUtils.highLevelOperationLock) {
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

            synchronized (ServicesUtils.highLevelOperationLock) {
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

    public String storePolicySet(String alias, String idPrefix, PolicySetType policySet)
            throws RemoteException {
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

            synchronized (ServicesUtils.highLevelOperationLock) {
                return UpdatePolicyOperation.instance(alias, version, policy).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public boolean updatePolicySet(String alias, String version, PolicySetType policySet)
            throws RemoteException {
        log.info(String.format("updatePolicySet(version=\"%s\", id=\"%s\");",
                               version,
                               policySet.getPolicySetId()));

        try {

            synchronized (ServicesUtils.highLevelOperationLock) {
                return UpdatePolicySetOperation.instance(alias, version, policySet).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }
}
