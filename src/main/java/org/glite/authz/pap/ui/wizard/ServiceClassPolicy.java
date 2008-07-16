package org.glite.authz.pap.ui.wizard;

import java.util.List;

import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class ServiceClassPolicy extends PolicyWizard {

    private static final String ID_PREFIX = "ServiceClassPolicy_";

    @Deprecated
    public static PolicyType build(List<AttributeWizard> targetAttributeList,
	    List<List<AttributeWizard>> exceptionsAttributeList,
	    EffectType effect) {

	return PolicyWizard.build(generateServiceClassPolicyId(),
		targetAttributeList, exceptionsAttributeList, effect);
    }

    private static String generateServiceClassPolicyId() {
	return ID_PREFIX + generateRandomLong();
    }

    public ServiceClassPolicy(List<AttributeWizard> targetAttributeList,
	    List<List<AttributeWizard>> exceptionsAttributeList,
	    EffectType effect) {
	super(generateServiceClassPolicyId(), targetAttributeList,
		exceptionsAttributeList, effect);
    }

}
