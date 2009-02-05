package org.glite.authz.pap.ui.wizard;

import java.util.List;

import org.opensaml.xacml.policy.EffectType;

public class BlacklistPolicy extends PolicyWizard {

    public BlacklistPolicy(List<AttributeWizard> targetAttributeList,
            List<List<AttributeWizard>> orExceptionsAttributeList) {
        
        //super(generateBlacklistPolicyId(), targetAttributeList, orExceptionsAttributeList, EffectType.Deny);
        super(targetAttributeList, orExceptionsAttributeList, EffectType.Deny);
        
    }
}
