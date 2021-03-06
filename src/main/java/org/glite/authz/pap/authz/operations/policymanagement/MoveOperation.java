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

package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveOperation extends BasePAPOperation<Object> {

    private static final Logger log = LoggerFactory.getLogger(MoveOperation.class);

    String alias;
    String id;
    String pivotId;
    boolean moveAfter;

    protected MoveOperation(String alias, String id, String pivotId, boolean moveAfter) {
        this.alias = alias;
        this.id = id;
        this.pivotId = pivotId;
        this.moveAfter = moveAfter;
    }

    public static MoveOperation instance(String alias, String id, String pivotId, boolean moveAfter) {
        return new MoveOperation(alias, id, pivotId, moveAfter);
    }

    protected Object doExecute() {

        if ((id == null) || (pivotId == null)) {
            return null;
        }

        if (id.equals(pivotId)) {
            return null;
        }

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }

        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote pap");
        }

        PapContainer papContainer = new PapContainer(pap);

        if (papContainer.hasPolicySet(id)) {

            movePolicySet(papContainer);

        } else if (papContainer.hasPolicy(id)) {

            movePolicy(papContainer);

        } else {
            moveRule(papContainer);
        }

        return null;
    }

    private void movePolicySet(PapContainer papContainer) {

        // now we have only two levels so... all the policy sets (resource <id>) are referenced by
        // the PAP
        // root policy set
        PolicySetType rootPAPPolicySet = papContainer.getRootPolicySet();

        if (!(PolicySetHelper.hasPolicySetReferenceId(rootPAPPolicySet, pivotId))) {
            throw new RepositoryException("Id not found or not a resource-id: " + pivotId);
        }

        if (!(PolicySetHelper.deletePolicySetReference(rootPAPPolicySet, id))) {
            throw new RepositoryException(String.format("Id \"%s\" not found into resource \"%s\"",
                                                        id,
                                                        rootPAPPolicySet.getPolicySetId()));
        }

        int pivotIndex = PolicySetHelper.getPolicySetIdReferenceIndex(rootPAPPolicySet, pivotId);

        if (moveAfter) {
            pivotIndex++;
        }
        log.debug("New position for PolicySet is: " + pivotIndex);

        PolicySetHelper.addPolicySetReference(rootPAPPolicySet, pivotIndex, id);

        String version = rootPAPPolicySet.getVersion();

        PolicySetWizard.increaseVersion(rootPAPPolicySet);

        papContainer.updatePolicySet(version, rootPAPPolicySet);
    }

    private void movePolicy(PapContainer papContainer) {
        PolicySetType targetPolicySet = null;

        // get the target policy set
        for (PolicySetType policySet : papContainer.getAllPolicySets()) {

            PolicySetType tempPolicySet = policySet;

            if (PolicySetHelper.hasPolicyReferenceId(tempPolicySet, pivotId)) {
                targetPolicySet = tempPolicySet;
                break;
            }
        }

        if (targetPolicySet == null) {
            throw new RepositoryException("Id not found or not an action-id: " + pivotId);
        }

        if (!(PolicySetHelper.deletePolicyReference(targetPolicySet, id))) {
            throw new RepositoryException(String.format("Id \"%s\" not found into resource \"%s\"",
                                                        id,
                                                        targetPolicySet.getPolicySetId()));
        }

        int pivotIndex = PolicySetHelper.getPolicyIdReferenceIndex(targetPolicySet, pivotId);

        if (moveAfter) {
            pivotIndex++;
        }
        log.debug("New position for Policy is: " + pivotIndex);

        PolicySetHelper.addPolicyReference(targetPolicySet, pivotIndex, id);

        String version = targetPolicySet.getVersion();

        PolicySetWizard.increaseVersion(targetPolicySet);

        papContainer.updatePolicySet(version, targetPolicySet);
    }

    private void moveRule(PapContainer papContainer) {

        PolicyType targetPolicy = null;

        for (PolicyType policy : papContainer.getAllPolicies()) {
            if (PolicyHelper.hasRuleWithId(policy, pivotId)) {
                targetPolicy = policy;
                break;
            }
        }

        if (targetPolicy == null) {
            throw new RepositoryException("Id not found or not a rule-id: " + pivotId);
        }

        RuleType rule = PolicyHelper.removeRule(targetPolicy, id);

        if (rule == null) {
            throw new RepositoryException(String.format("Id \"%s\" not found into action \"%s\"",
                                                        id,
                                                        targetPolicy.getPolicyId()));
        }

        int pivotIndex = PolicyHelper.indexOfRule(targetPolicy, pivotId);

        if (moveAfter) {
            pivotIndex++;
        }

        PolicyHelper.addRule(targetPolicy, pivotIndex, rule);

        String version = targetPolicy.getVersion();

        PolicyWizard.increaseVersion(targetPolicy);

        papContainer.updatePolicy(version, targetPolicy);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }
}
