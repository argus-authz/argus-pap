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

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;

public class RemoveObjectByIdAndReferencesOperation extends BasePAPOperation<Boolean> {

    String alias;
    String id;

    private RemoveObjectByIdAndReferencesOperation(String alias, String id) {
        this.alias = alias;
        this.id = id;
    }

    public static RemoveObjectByIdAndReferencesOperation instance(String alias, String id) {

        return new RemoveObjectByIdAndReferencesOperation(alias, id);
    }

    @Override
    protected Boolean doExecute() {

        if (id == null) {
            throw new XACMLPolicyManagementServiceException("id is null");
        }

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }

        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        if (papContainer.getRootPolicySetId().equals(id)) {
            throw new RepositoryException("Invalid operation: cannot remove the local PAP root policy set");
        }

        if (papContainer.hasPolicy(id)) {
            papContainer.removePolicyAndReferences(id);
            return true;
        }

        if (papContainer.hasPolicySet(id)) {
            papContainer.removePolicySetAndReferences(id);
            return true;
        }

        removeRule(papContainer, id);

        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

    private void removeRule(PapContainer papContainer, String id) {
        List<PolicyType> policyList = papContainer.getAllPolicies();
        PolicyType targetPolicy = null;
        RuleType targetRule = null;

        for (PolicyType policy : policyList) {
            List<RuleType> ruleList = policy.getRules();

            for (RuleType rule : ruleList) {
                if (id.equals(rule.getRuleId())) {
                    targetRule = rule;
                    break;
                }
            }

            if (targetRule != null) {
                ruleList.remove(targetRule);
                targetPolicy = policy;
                break;
            }
        }

        if (targetPolicy == null) {
            throw new NotFoundException("Id not found: " + id);
        }

        if (targetPolicy.getRules().size() == 0) {
            
            papContainer.removePolicyAndReferences(targetPolicy.getPolicyId());
            
        } else {
            String version = targetPolicy.getVersion();

            PolicyWizard.increaseVersion(targetPolicy);

            papContainer.updatePolicy(version, targetPolicy);
        }
    }
}
