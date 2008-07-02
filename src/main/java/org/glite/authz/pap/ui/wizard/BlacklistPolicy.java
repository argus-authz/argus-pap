package org.glite.authz.pap.ui.wizard;

import java.util.List;
import java.util.Random;

import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class BlacklistPolicy {
	
	private BlacklistPolicy() {}
	
	public static PolicyType build(List<AttributeWizard> targetAttributeList,
			List<List<AttributeWizard>> exceptionsAttributeList) {
		
		Random generator = new Random();
		String id = "blacklist_" + generator.nextLong(); 
		
		return PolicyWizard.build(id, targetAttributeList, exceptionsAttributeList, EffectType.Deny);
	}

}
