package org.glite.authz.pap.ui.wizard;

import java.util.List;
import java.util.Random;

import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class ServiceClassPolicy {
	
	private ServiceClassPolicy() {}
	
	public static PolicyType build(List<AttributeWizard> targetAttributeList,
			List<AttributeWizard> exceptionsAttributeList, EffectType effect) {
		
		Random generator = new Random();
		String id = "serviceclass_" + generator.nextLong(); 
		
		return PolicyWizard.build(id, targetAttributeList, exceptionsAttributeList, effect);
	}

}
