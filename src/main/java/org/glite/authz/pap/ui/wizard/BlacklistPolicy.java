package org.glite.authz.pap.ui.wizard;

import java.util.List;

import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class BlacklistPolicy extends PolicyWizard {

    private static final String ID_PREFIX = "BlacklistPolicy_";

    @Deprecated
    public static PolicyType build(List<AttributeWizard> targetAttributeList,
            List<List<AttributeWizard>> exceptionsAttributeList) {

        return PolicyWizard.build(generateBlacklistPolicyId(), targetAttributeList,
                exceptionsAttributeList, EffectType.Deny);
    }

    private static String generateBlacklistPolicyId() {
        return ID_PREFIX + generateRandomLong();
    }

    public BlacklistPolicy(List<AttributeWizard> targetAttributeList,
            List<List<AttributeWizard>> orExceptionsAttributeList) {
        
        super(generateBlacklistPolicyId(), targetAttributeList, orExceptionsAttributeList,
                EffectType.Deny);
    }
}
