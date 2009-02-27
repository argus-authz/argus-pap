package org.glite.authz.pap.common.xacml.wizard;

import java.util.List;

import org.glite.authz.pap.common.xacml.utils.RuleHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.RuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionsRuleWizard {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ExceptionsRuleWizard.class);
    private static final String ruleId = "ExceptionsRule";

    public static List<List<AttributeWizard>> getConditionAttributeWizardListList(RuleType rule) {

        if (!ruleId.equals(rule.getRuleId())) {
            throw new UnsupportedPolicyException("Unrecognized RuleId");
        }
        
        List<List<AttributeWizard>> resultList = ConditionWizard.getAttributeWizardListList(rule.getCondition());

        return resultList;
    }
    
    public static List<AttributeWizard> getTargetAttributeWizardList(RuleType rule) {
        
        if (!ruleId.equals(rule.getRuleId())) {
            throw new UnsupportedPolicyException("Unrecognized RuleId");
        }
        
        TargetWizard targetWizard = new TargetWizard(rule.getTarget());
        
        List<AttributeWizard> resultList = targetWizard.getAttributeWizardList();
        
        return resultList;
    }

    public static RuleType getXACML(List<AttributeWizard> targetAttributeWizardList,
            List<List<AttributeWizard>> orExceptionsAttributeWizardList, EffectType effect) {

        RuleType exceptionsRule = RuleHelper.build(ruleId, effect);

        TargetWizard targetWizard = new TargetWizard(targetAttributeWizardList);

        ConditionType condition = ConditionWizard.getXACML(orExceptionsAttributeWizardList);

        exceptionsRule.setTarget(targetWizard.getXACML());
        exceptionsRule.setCondition(condition);

        return exceptionsRule;
    }

}
