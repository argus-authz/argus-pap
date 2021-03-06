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

package org.glite.authz.pap.authz.operations.policyprovisioning;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.ServicesUtils;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

/**
 * Authorized operation to fetch policies out of this PAP when the caller is another PAP.
 */
public class GetPoliciesForPAPOperation extends BasePAPOperation<List<XACMLObject>> {

    private GetPoliciesForPAPOperation() {}

    /**
     * Returns an instance of this operation
     * 
     * @return
     */
    public static GetPoliciesForPAPOperation instance() {
        return new GetPoliciesForPAPOperation();
    }

    @Override
    protected List<XACMLObject> doExecute() {

        log.debug("Executing PAP query...");

        List<XACMLObject> resultList = new LinkedList<XACMLObject>();

        List<PapContainer> papContainerList = new ArrayList<PapContainer>(PapContainer.getContainers(PapManager.getInstance()
                                                                                                               .getPublicPaps()));

        PolicySetType rootPolicySet = ServicesUtils.makeRootPolicySet();
        resultList.add(rootPolicySet);

        for (PapContainer papContainer : papContainerList) {

            List<XACMLObject> papXACMLObjectList = getPublic(papContainer);

            if (papXACMLObjectList.size() > 0) {
                PolicySetType papRootPolicySet = (PolicySetType) papXACMLObjectList.get(0);

                List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(papRootPolicySet);
                List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(papRootPolicySet);

                TypeStringUtils.releaseUnneededMemory(papRootPolicySet);
                for (String id : policySetIdList) {
                    PolicySetHelper.addPolicySetReference(rootPolicySet, id);
                }
                for (String id : policyIdList) {
                    PolicySetHelper.addPolicySetReference(rootPolicySet, id);
                }

                papXACMLObjectList.remove(papRootPolicySet);

                resultList.addAll(papXACMLObjectList);
            }
        }

        log.debug("PAP query executed: retrieved " + resultList.size() + " elements (Policy/PolicySet)");

        return resultList;
    }

    @Override
    /*
     * * The required permission for this operation is: <code>POLICY_READ_LOCAL</code>
     */
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }

    private List<XACMLObject> getPublic(PapContainer papContainer) {

        List<XACMLObject> resultList = new LinkedList<XACMLObject>();

        List<PolicySetType> resultPolicySetList = new LinkedList<PolicySetType>();

        List<PolicySetType> policySetList;
        List<PolicyType> policyList;

        if (papContainer.getPap().isLocal()) {
            synchronized (ServicesUtils.highLevelOperationLock) {
                policySetList = papContainer.getAllPolicySets();
                policyList = papContainer.getAllPolicies();
            }
        } else {
            synchronized (DistributionModule.storePoliciesLock) {
                policySetList = papContainer.getAllPolicySets();
                policyList = papContainer.getAllPolicies();
            }
        }

        for (PolicySetType policySet : policySetList) {
            resultPolicySetList.add(policySet);
        }

        List<PolicyType> resultPolicyList = new LinkedList<PolicyType>();

        boolean removedAtLeastOnePrivatePolicy = false;

        // Remove all private policies from the list (together with its references in the policy
        // sets)
        for (PolicyType policy : policyList) {
            String policyId = policy.getPolicyId();

            TypeStringUtils.releaseUnneededMemory(policy);

            if (PolicyWizard.isPublic(policyId)) {
                resultPolicyList.add(policy);
                continue;
            }

            // Remove references of the policy
            for (PolicySetType policySet : resultPolicySetList) {
                if (PolicySetHelper.deletePolicyReference(policySet, policyId)) {
                    removedAtLeastOnePrivatePolicy = true;
                }
            }
        }

        if (removedAtLeastOnePrivatePolicy) {
            for (PolicySetType policySet : resultPolicySetList) {
                TypeStringUtils.releaseUnneededMemory(policySet);
            }
        }

        log.debug("Adding " + resultPolicySetList.size() + " PolicySet elements from PAP \""
                + papContainer.getPap().getId() + "\"");
        resultList.addAll(resultPolicySetList);

        log.debug("Adding " + policyList.size() + " Policy elements from PAP \""
                + papContainer.getPap().getId() + "\"");
        resultList.addAll(resultPolicyList);

        return resultList;
    }
}
