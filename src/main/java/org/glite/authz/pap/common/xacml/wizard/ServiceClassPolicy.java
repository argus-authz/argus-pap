package org.glite.authz.pap.common.xacml.wizard;

import java.util.List;

import org.opensaml.xacml.policy.EffectType;

public class ServiceClassPolicy extends PolicyWizard {

    public ServiceClassPolicy(List<AttributeWizard> targetAttributeList,
            List<List<AttributeWizard>> exceptionsAttributeList, EffectType effect) {
        
        //super(generateServiceClassPolicyId(), targetAttributeList, exceptionsAttributeList, effect);
        super(targetAttributeList, exceptionsAttributeList, effect);
        
    }

}
