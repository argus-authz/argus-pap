package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;

public class AddPoliciesOperation extends BasePAPOperation<String[]> {

	int index;
	PolicyType[] policy;
	String[] policyIdPrefix;
	String policySetId;

	protected AddPoliciesOperation(int index, String policySetId, String[] policyIdPrefix, PolicyType[] policy) {
		this.index = index;
		this.policySetId = policySetId;
		this.policyIdPrefix = policyIdPrefix;
		this.policy = policy;
	}

	public static AddPoliciesOperation instance(int index, String policySetId, String policyIdPrefix[],
			PolicyType[] policy) {
		return new AddPoliciesOperation(index, policySetId, policyIdPrefix, policy);
	}

	protected String[] doExecute() {

		PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

		if (!localPAP.hasPolicySet(policySetId)) {
			log.warn(String.format("Policy not added because PolicySetId \"%s\" does not exists.",
				policySetId));
			return null;
		}

		String[] policyIdArray = new String[policy.length];

		for (int i = 0; i < policy.length; i++) {

			policyIdArray[i] = WizardUtils.generateId(policyIdPrefix[i]);
			policy[i].setPolicyId(policyIdArray[i]);

			if (index == -1) {
				localPAP.addPolicy(index, policySetId, policy[i]);
			} else {
				localPAP.addPolicy(index + i, policySetId, policy[i]);
			}
			TypeStringUtils.releaseUnnecessaryMemory(policy[i]);

			log.info(String.format("Added policy (policyId=\"%s\")", policyIdArray[i]));
		}
		return policyIdArray;
	}

	@Override
	protected void setupPermissions() {
		addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
	}
}
