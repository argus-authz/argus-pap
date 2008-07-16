package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.Configuration;

public class PolicyHelper extends XACMLHelper<PolicyType> {

    public static final String RULE_COMBALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
    public static final String RULE_COMBALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
    public static final String RULE_COMBALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

    private static PolicyHelper instance = null;

    public static PolicyType build(String policyId,
	    String ruleCombinerAlgorithmId, TargetType target) {
	PolicyType policy = (PolicyType) Configuration.getBuilderFactory()
		.getBuilder(PolicyType.DEFAULT_ELEMENT_NAME).buildObject(
			PolicyType.DEFAULT_ELEMENT_NAME);
	policy.setPolicyId(policyId);
	policy.setRuleCombiningAlgoId(ruleCombinerAlgorithmId);
	if (target == null) {
	    policy.setTarget(TargetHelper.buildAnyTarget());
	} else {
	    policy.setTarget(target);
	}
	return policy;
    }

    public static PolicyType buildWithAnyTarget(String policyId,
	    String ruleCombinerAlgorithmId) {
	return build(policyId, ruleCombinerAlgorithmId, null);
    }

    public static PolicyHelper getInstance() {
	if (instance == null) {
	    instance = new PolicyHelper();
	}
	return instance;
    }

    private PolicyHelper() {
    }

}
