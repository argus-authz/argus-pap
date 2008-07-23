package org.glite.authz.pap.ui.wizard;

import java.util.List;
import java.util.UUID;

import org.opensaml.xacml.policy.EffectType;

public class BlacklistPolicy extends PolicyWizard {

    private static final String ID_PREFIX = "BlacklistPolicy_";

    private static String generateBlacklistPolicyId() {
        return ID_PREFIX + UUID.randomUUID().toString();
    }

    public BlacklistPolicy(List<AttributeWizard> targetAttributeList,
            List<List<AttributeWizard>> orExceptionsAttributeList) {
        
        super(generateBlacklistPolicyId(), targetAttributeList, orExceptionsAttributeList,
                EffectType.Deny);
    }
}
