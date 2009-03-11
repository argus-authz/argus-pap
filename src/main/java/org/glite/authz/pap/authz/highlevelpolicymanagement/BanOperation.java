package org.glite.authz.pap.authz.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
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

	public static BanOperation instance(AttributeWizard banAttributeWizard,
			AttributeWizard resourceAttributeWizard, AttributeWizard actionAttributeWizard, boolean isPublic) {
		return new BanOperation(banAttributeWizard, resourceAttributeWizard, actionAttributeWizard, isPublic);
	}

	protected String doExecute() {

		PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

		// get the target policy set, it must be the very first policy set (if
		// it exists)
		PolicySetType targetPolicySet = null;
		TargetWizard policySetTargetWizard = new TargetWizard(resourceAttributeWizard);

		List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(localPAP
				.getPAPRootPolicySet());

		boolean updateOperationForPolicySet = false;
		if (policySetIdList.size() > 0) {
			PolicySetType candidatePolicySet = TypeStringUtils.cloneAsPolicySetTypeString(localPAP
					.getPolicySet(policySetIdList.get(0)));
			if (policySetTargetWizard.isEquivalent(candidatePolicySet.getTarget())) {
				targetPolicySet = candidatePolicySet;
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
			PolicyType candidatePolicy = TypeStringUtils.cloneAsPolicyTypeString(localPAP
					.getPolicy(policyIdList.get(0)));
			if (policyTargetWizard.isEquivalent(candidatePolicy.getTarget())) {
				
				targetPolicyWizard = new PolicyWizard(candidatePolicy);

				if (targetPolicyWizard.isPublic() != isPublic) {
					targetPolicyWizard = null;
				} else {
					if (targetPolicyWizard.denyRuleForAttributeExists(banAttributeWizard)) {
						return null;
					}
					policyId = candidatePolicy.getPolicyId();
					updateOperationForPolicy = true;
					updateOperationForPolicySet = false;
				}
			}
		}

		if (targetPolicyWizard == null) {
			targetPolicyWizard = new PolicyWizard(actionAttributeWizard);
			targetPolicyWizard.setPrivate(!isPublic);
			policyId = targetPolicyWizard.getPolicyId();
			PolicySetHelper.addPolicyReference(targetPolicySet, 0, policyId);
		}

		if (updateOperationForPolicySet) {
			String oldVersion = targetPolicySet.getVersion();
			PolicySetWizard.increaseVersion(targetPolicySet);
			localPAP.updatePolicySet(oldVersion, targetPolicySet);
		} else {
			if (!updateOperationForPolicy) {
				localPAP.addPolicySet(0, targetPolicySet);
			}
		}

		targetPolicyWizard.addRule(0, banAttributeWizard, EffectType.Deny);

		if (updateOperationForPolicy) {
			String oldVersion = targetPolicyWizard.getVersionString();
			targetPolicyWizard.increaseVersion();
			log.debug(XMLObjectHelper.toString(targetPolicyWizard.getXACML()));
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

}
