package org.glite.authz.pap.authz.operations.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.papmanagement.PAPContainer;
import org.glite.authz.pap.papmanagement.PAPManager;
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
            alias = PAP.DEFAULT_PAP_ALIAS;
        }
        
        PAP pap = PAPManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

        if (papContainer.getPAPRootPolicySetId().equals(id)) {
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
    
    private void removeRule(PAPContainer papContainer, String id) {
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
            throw new NotFoundException("RuleId not found: " + id);
        }
        
        String version = targetPolicy.getVersion();
        
        PolicyWizard.increaseVersion(targetPolicy);
        
        papContainer.updatePolicy(version, targetPolicy);
    }
}
