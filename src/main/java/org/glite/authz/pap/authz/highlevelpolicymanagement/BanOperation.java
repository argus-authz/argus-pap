package org.glite.authz.pap.authz.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.TargetWizard;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class BanOperation extends BasePAPOperation<String> {

    AttributeWizard banAttributeWizard;
    AttributeWizard resourceAttributeWizard;
    AttributeWizard actionAttributeWizard;
    String attributeValue;
    String description;
    boolean isPublic;

    protected BanOperation(AttributeWizard banAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard, boolean isPublic, String description) {

        this.banAttributeWizard = banAttributeWizard;
        this.resourceAttributeWizard = resourceAttributeWizard;
        this.actionAttributeWizard = actionAttributeWizard;
        this.description = description;
        this.isPublic = isPublic;
    }

    public static BanOperation instance(AttributeWizard banAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard, boolean isPublic, String description) {
        return new BanOperation(banAttributeWizard, resourceAttributeWizard, actionAttributeWizard, isPublic, description);
    }

    protected String doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        // get the target policy set, it must be the very first policy set (if it exists)
        PolicySetType targetPolicySet = null;
        TargetWizard policySetTargetWizard = new TargetWizard(resourceAttributeWizard);

        List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(localPAP.getPAPRootPolicySet());

        boolean updateOperationForPolicySet = false;
        if (policySetIdList.size() > 0) {
            PolicySetType policySet = localPAP.getPolicySet(policySetIdList.get(0));
            if (policySetTargetWizard.isEqual(policySet.getTarget())) {
                targetPolicySet = policySet;
                updateOperationForPolicySet = true;
            }
        }

        if (targetPolicySet == null) {
            targetPolicySet = (new PolicySetWizard(resourceAttributeWizard)).getXACML();
        }

        PolicyWizard targetPolicyWizard = null;
        TargetWizard policyTargetWizard = new TargetWizard(actionAttributeWizard);
        boolean updateOperationForPolicy = false;
        String policyId = null;

        // get the target policy, it must be the very first policy
        List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(targetPolicySet);

        if (policyIdList.size() > 0) {
            PolicyType policy = localPAP.getPolicy(policyIdList.get(0));
            if (policyTargetWizard.isEqual(policy.getTarget())) {

                targetPolicyWizard = new PolicyWizard(policy);

                if (targetPolicyWizard.isPublic() != isPublic) {
                    targetPolicyWizard = null;
                } else {
                    if (targetPolicyWizard.denyRuleForAttributeExists(banAttributeWizard)) {
                        return null;
                    }
                    policyId = policy.getPolicyId();
                    updateOperationForPolicy = true;
                    updateOperationForPolicySet = false;
                }
            }
        }

        if (targetPolicyWizard == null) {
            targetPolicyWizard = new PolicyWizard(actionAttributeWizard);
            targetPolicyWizard.setVersion(0);
            targetPolicyWizard.setPrivate(!isPublic);
            policyId = targetPolicyWizard.getPolicyId();
            PolicySetHelper.addPolicyReference(targetPolicySet, 0, policyId);
        }

        targetPolicyWizard.addRule(0, banAttributeWizard, EffectType.Deny);
        targetPolicyWizard.increaseVersion();

        if (updateOperationForPolicySet) {
            localPAP.updatePolicySet(targetPolicySet);
        } else {
            if (!updateOperationForPolicy) {
                localPAP.addPolicySet(-1, targetPolicySet);
            }
        }

        if (updateOperationForPolicy) {
            localPAP.updatePolicy(targetPolicyWizard.getXACML());
        } else {
            localPAP.storePolicy(targetPolicyWizard.getXACML());
        }

        return policyId;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
