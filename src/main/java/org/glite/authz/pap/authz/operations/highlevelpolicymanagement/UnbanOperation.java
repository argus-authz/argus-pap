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

package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.TargetWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnbanOperation extends BasePAPOperation<UnbanResult> {

    private static final Logger log = LoggerFactory.getLogger(UnbanOperation.class);

    private String alias;
    private final AttributeWizard actionAttributeWizard;
    private final AttributeWizard bannedAttributeWizard;
    private final AttributeWizard resourceAttributeWizard;

    protected UnbanOperation(String alias, AttributeWizard bannedAttributeWizard,
            AttributeWizard resourceAttributeWizard, AttributeWizard actionAttributeWizard) {

        this.alias = alias;
        this.bannedAttributeWizard = bannedAttributeWizard;
        this.resourceAttributeWizard = resourceAttributeWizard;
        this.actionAttributeWizard = actionAttributeWizard;
    }

    public static UnbanOperation instance(String alias, AttributeWizard bannedAttributeWizard,
            AttributeWizard resourceAttributeWizard, AttributeWizard actionAttributeWizard) {
        return new UnbanOperation(alias,
                                  bannedAttributeWizard,
                                  resourceAttributeWizard,
                                  actionAttributeWizard);
    }

    protected UnbanResult doExecute() {

        UnbanResult unbanResult = new UnbanResult();
        unbanResult.setConflictingPolicies(new String[0]);

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }

        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        List<PolicySetType> targetPolicySetList = getTargetPolicySetList(papContainer);

        if (targetPolicySetList.isEmpty()) {
            log.debug("targetPolicySet not found");
            unbanResult.setStatusCode(1);
            return unbanResult;
        }

        PolicyType targetPolicy = null;

        for (PolicySetType targetPolicySet : targetPolicySetList) {
            targetPolicy = getTargetPolicy(papContainer, targetPolicySet);
            if (targetPolicy != null) {
                break;
            }
        }

        if (targetPolicy == null) {
            log.debug("targetPolicy not found");
            unbanResult.setStatusCode(1);
            return unbanResult;
        }

        PolicyWizard policyWizard = new PolicyWizard(targetPolicy);
        TypeStringUtils.releaseUnneededMemory(targetPolicy);

        if (policyWizard.removeDenyRuleForAttribute(bannedAttributeWizard)) {
            log.debug("ban rule found, updating policy");
            
            if (policyWizard.getNumberOfRules() == 0) {
                
                papContainer.removePolicyAndReferences(policyWizard.getPolicyId());
                
            } else {

                String oldVersion = policyWizard.getVersionString();
                policyWizard.increaseVersion();
                papContainer.updatePolicy(oldVersion, policyWizard.getXACML());
            }

            unbanResult.setStatusCode(0);
            return unbanResult;
        }

        unbanResult.setStatusCode(1);
        return unbanResult;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE,
                                               PermissionFlags.POLICY_READ_LOCAL));
    }

    private PolicyType getTargetPolicy(PapContainer papContainer, PolicySetType targetPolicySet) {

        List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(targetPolicySet);
        TypeStringUtils.releaseUnneededMemory(targetPolicySet);

        TargetWizard policyTargetWizard = new TargetWizard(actionAttributeWizard);

        for (String policyId : policyIdList) {

            PolicyType repositoryPolicy = papContainer.getPolicy(policyId);

            if (policyTargetWizard.isEquivalent(repositoryPolicy.getTarget())) {
                return repositoryPolicy;
            }
            TypeStringUtils.releaseUnneededMemory(repositoryPolicy);
        }
        return null;
    }

    private List<PolicySetType> getTargetPolicySetList(PapContainer papContainer) {

        List<PolicySetType> targetPolicySetList = new LinkedList<PolicySetType>();

        TargetWizard policySetTargetWizard = new TargetWizard(resourceAttributeWizard);

        PolicySetType rootPAPPolicySet = papContainer.getRootPolicySet();

        List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(rootPAPPolicySet);

        TypeStringUtils.releaseUnneededMemory(rootPAPPolicySet);

        for (String policySetId : policySetIdList) {

            PolicySetType policySet = papContainer.getPolicySet(policySetId);

            if (policySetTargetWizard.isEquivalent(policySet.getTarget())) {
                targetPolicySetList.add(policySet);
            }

            TypeStringUtils.releaseUnneededMemory(policySet);
        }
        return targetPolicySetList;
    }
}
