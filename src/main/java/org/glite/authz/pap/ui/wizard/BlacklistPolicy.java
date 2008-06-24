package org.glite.authz.pap.ui.wizard;

import java.util.List;

import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class BlacklistPolicy {
	
	private BlacklistPolicy() {}
	
	public static PolicyType build(List<WizardAttribute> targetAttributeList,
			List<WizardAttribute> exceptionsAttributeList) {
		return PolicyWizard.build(targetAttributeList, exceptionsAttributeList, EffectType.Deny);
	}

}
