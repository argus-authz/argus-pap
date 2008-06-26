package org.glite.authz.pap.ui.wizard;

import java.util.List;

import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class ServiceClassPolicy {
	
	private ServiceClassPolicy() {}
	
	public static PolicyType build(List<AttributeWizard> targetAttributeList,
			List<AttributeWizard> exceptionsAttributeList, EffectType effect) {
		return PolicyWizard.build(targetAttributeList, exceptionsAttributeList, effect);
	}

}
