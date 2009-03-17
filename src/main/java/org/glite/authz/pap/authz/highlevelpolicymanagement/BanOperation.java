package org.glite.authz.pap.authz.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
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

    private AttributeWizard actionAttributeWizard;
    private AttributeWizard banAttributeWizard;
    private boolean isPublic;
    private AttributeWizard resourceAttributeWizard;

    protected BanOperation(AttributeWizard banAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard, boolean isPublic) {

        this.banAttributeWizard = banAttributeWizard;
        this.resourceAttributeWizard = resourceAttributeWizard;
        this.actionAttributeWizard = actionAttributeWizard;
        this.isPublic = isPublic;
    }

    public static BanOperation instance(AttributeWizard banAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard, boolean isPublic) {
        return new BanOperation(banAttributeWizard, resourceAttributeWizard, actionAttributeWizard, isPublic);
    }

    protected String doExecute() {

		boolean policySetNeedToBeSaved = true;
		boolean updateOperationForPolicySet = false;
		boolean updateOperationForPolicy = false;
		PAPContainer localPAP = PAPManager.getInstance().getDefaultPAPContainer();

		PolicySetType targetPolicySet = getTargetPolicySet(localPAP);

		if (targetPolicySet == null) {
			targetPolicySet = (new PolicySetWizard(resourceAttributeWizard)).getXACML();
		} else {
			updateOperationForPolicySet = true;
		}

		String policyId = null;

		PolicyWizard targetPolicyWizard;
		PolicyType candidatePolicy = getTargetPolicy(localPAP, targetPolicySet);

		if (candidatePolicy == null) {
			targetPolicyWizard = new PolicyWizard(actionAttributeWizard);
			targetPolicyWizard.setPrivate(!isPublic);
			policyId = targetPolicyWizard.getPolicyId();
			PolicySetHelper.addPolicyReference(targetPolicySet, 0, policyId);
		} else {
			targetPolicyWizard = new PolicyWizard(candidatePolicy);

			if (targetPolicyWizard.denyRuleForAttributeExists(banAttributeWizard)) {
				// ban policy already exists
				return null;
			}
			policyId = candidatePolicy.getPolicyId();
			updateOperationForPolicy = true;
			policySetNeedToBeSaved = false;
		}

		targetPolicyWizard.addRule(0, banAttributeWizard, EffectType.Deny);

		// Store the ban policy and the policy set in which it is contained
		// (only if needed)
		if (policySetNeedToBeSaved) {
			if (updateOperationForPolicySet) {
				String oldVersion = targetPolicySet.getVersion();
				PolicySetWizard.increaseVersion(targetPolicySet);
				localPAP.updatePolicySet(oldVersion, targetPolicySet);
			} else {
				localPAP.addPolicySet(0, targetPolicySet);
			}
		} else {
			TypeStringUtils.releaseUnneededMemory(targetPolicySet);
		}

		if (updateOperationForPolicy) {
			String oldVersion = targetPolicyWizard.getVersionString();
			targetPolicyWizard.increaseVersion();
			localPAP.updatePolicy(oldVersion, targetPolicyWizard.getXACML());
		} else {
			localPAP.storePolicy(targetPolicyWizard.getXACML());
		}

		targetPolicyWizard.releaseChildrenDOM();
		targetPolicyWizard.releaseDOM();

		return policyId;
	}

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }

    private PolicyType getTargetPolicy(PAPContainer papContainer, PolicySetType policySet) {

        List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(policySet);

        if (policyIdList.size() == 0) {
            return null;
        }

        // get the target policy, it must be the very first policy
        PolicyType candidatePolicy = papContainer.getPolicy(policyIdList.get(0));

        PolicyType policy = null;;
        TargetWizard policyTargetWizard = new TargetWizard(actionAttributeWizard);

        if (policyTargetWizard.isEquivalent(candidatePolicy.getTarget())) {

            policy = candidatePolicy;

            if (PolicyWizard.isPublic(policy.getPolicyId()) != isPublic) {
                return null;
            }
        }
        return policy;
    }

    private PolicySetType getTargetPolicySet(PAPContainer papContainer) {

        // get the target policy set, it must be the very first policy set (if it exists)
        PolicySetType targetPolicySet = null;
        TargetWizard policySetTargetWizard = new TargetWizard(resourceAttributeWizard);

        PolicySetType papRootPolicySet = papContainer.getPAPRootPolicySet();

        List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(papRootPolicySet);

        TypeStringUtils.releaseUnneededMemory(papRootPolicySet);

        if (policySetIdList.size() == 0) {
            return null;
        }

        PolicySetType candidatePolicySet = papContainer.getPolicySet(policySetIdList.get(0));

        if (policySetTargetWizard.isEquivalent(candidatePolicySet.getTarget())) {
            targetPolicySet = candidatePolicySet;
        }

        return targetPolicySet;
    }
}
