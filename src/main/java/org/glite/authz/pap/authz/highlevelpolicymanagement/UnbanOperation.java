package org.glite.authz.pap.authz.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.TargetWizard;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class UnbanOperation extends BasePAPOperation<UnbanResult> {

    private final AttributeWizard bannedAttributeWizard;
    private final AttributeWizard resourceAttributeWizard;
    private final AttributeWizard actionAttributeWizard;

    protected UnbanOperation(AttributeWizard bannedAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard) {
        
        this.bannedAttributeWizard = bannedAttributeWizard;
        this.resourceAttributeWizard = resourceAttributeWizard;
        this.actionAttributeWizard = actionAttributeWizard;
    }

    public static UnbanOperation instance(AttributeWizard bannedAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard) {
        return new UnbanOperation(bannedAttributeWizard, resourceAttributeWizard, actionAttributeWizard);
    }

    protected UnbanResult doExecute() {

        TargetWizard policySetTargetWizard = new TargetWizard(resourceAttributeWizard);
        TargetWizard policyTargetWizard = new TargetWizard(actionAttributeWizard);
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        PolicySetType targetPolicySet = null;
        List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(localPAP.getPAPRootPolicySet());
        
        for (String policySetId : policySetIdList) {
            PolicySetType policySet = localPAP.getPolicySet(policySetId);
            if (policySetTargetWizard.isEquivalent(policySet.getTarget())) {
                targetPolicySet = policySet;
                break;
            }
        }
        
        UnbanResult unbanResult = new UnbanResult();
        unbanResult.setConflictingPolicies(new String[0]);
        
        if (targetPolicySet == null) {
            unbanResult.setStatusCode(1);
            return unbanResult;
        }
        
        List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(targetPolicySet);
        
        for (String policyId : policyIdList) {
            PolicyType policy = localPAP.getPolicy(policyId);
            if (policyTargetWizard.isEquivalent(policy.getTarget())) {
                
                PolicyWizard policyWizard = new PolicyWizard(policy);
                
                if (policyWizard.removeDenyRuleForAttribute(bannedAttributeWizard)) {
                    
                    if (policyWizard.getNumberOfRules() == 0) {
                        localPAP.removePolicyAndReferences(policyId);
                    } else {
                        localPAP.updatePolicy(policyWizard.getXACML());
                    }
                    
                    unbanResult.setStatusCode(0);
                    return unbanResult;
                }
                break;
            }
        }
        
        unbanResult.setStatusCode(1);
        return unbanResult;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE, PermissionFlags.POLICY_READ_LOCAL));

    }

}
