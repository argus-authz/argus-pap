package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.RuleType;

public class RuleHelper extends XMLObjectHelper<RuleType> {

    public static final String RULE_COMBALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
    public static final String RULE_COMBALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
    public static final String RULE_COMBALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";
    private static final javax.xml.namespace.QName elementQName = RuleType.DEFAULT_ELEMENT_NAME;
    private static final RuleHelper instance = new RuleHelper();

    private RuleHelper() {}

    public static RuleType build(String ruleId, EffectType effect) {
        RuleType rule = (RuleType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        rule.setRuleId(ruleId);
        rule.setEffect(effect);
        return rule;
    }

    public static RuleHelper getInstance() {
        return instance;
    }

}
