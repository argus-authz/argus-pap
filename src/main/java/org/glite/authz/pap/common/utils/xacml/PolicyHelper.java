package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;

public class PolicyHelper extends XMLObjectHelper<PolicyType> {

    public static final String RULE_COMBALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
    public static final String RULE_COMBALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
    public static final String RULE_COMBALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

    private static final javax.xml.namespace.QName ELEMENT_NAME = PolicyType.DEFAULT_ELEMENT_NAME;

    private static PolicyHelper instance = new PolicyHelper();

    public static PolicyType build(String policyId, String ruleCombinerAlgorithmId) {
        return build(policyId, ruleCombinerAlgorithmId, null);
    }

    public static PolicyType build(String policyId, String ruleCombinerAlgorithmId, TargetType target) {

        PolicyType policy = (PolicyType) builderFactory.getBuilder(ELEMENT_NAME).buildObject(
                ELEMENT_NAME);
        policy.setPolicyId(policyId);
        policy.setRuleCombiningAlgoId(ruleCombinerAlgorithmId);

        if (target == null)
            policy.setTarget(TargetHelper.build());
        else
            policy.setTarget(target);

        return policy;
    }

    public static PolicyHelper getInstance() {
        return instance;
    }

    private PolicyHelper() {}

}
