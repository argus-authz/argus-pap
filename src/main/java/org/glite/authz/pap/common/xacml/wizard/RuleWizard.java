package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.utils.RuleHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.WizardException;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.RuleType;

public class RuleWizard {
    
    private final RuleType rule;
    private final TargetWizard targetWizard;
    
    public RuleWizard(AttributeWizard attributeWizard, EffectType effect) {
        this(getList(attributeWizard), effect);
    }
    
    public RuleWizard(EffectType effect) {
        this(getList(null), effect);
    }
    
    public RuleWizard(List<AttributeWizard> targetAttributeWizardList, EffectType effect) {
        
        rule = RuleHelper.build(WizardUtils.generateId("Rule"), effect);
        
        if (targetAttributeWizardList == null) {
            targetAttributeWizardList = new ArrayList<AttributeWizard>(0);
        }
        
        targetWizard = new TargetWizard(targetAttributeWizardList);
        
        if (targetAttributeWizardList.size() == 0) {
            return;
        }

        rule.setTarget(targetWizard.getXACML());
    }
    
    public RuleWizard(RuleType rule) {
        
        if (rule == null) {
            throw new WizardException("Invalid argument: RuleType is null.");
        }
        
        this.rule = rule;
        
        targetWizard = new TargetWizard(rule.getTarget());
    }
    
    private static List<AttributeWizard> getList(AttributeWizard attributeWizard) {
        
        if (attributeWizard == null) {
            return new ArrayList<AttributeWizard>(0);
        }
        
        List<AttributeWizard> list = new ArrayList<AttributeWizard>(1);
        
        list.add(attributeWizard);
        
        return list;
    }
    
    public boolean deniesAttribute(AttributeWizard attributeWizard) {
        
        if (!(EffectType.Deny.equals(rule.getEffect()))) {
            return false;
        }
        
        for (AttributeWizard targetAttributeWizard : targetWizard.getAttributeWizardList()) {
            if (targetAttributeWizard.equals(attributeWizard)) {
                return true;
            }
        }
        
        return false;
    }
    
    public RuleType getXACML() {
        return rule;
    }
    
    public String getRuleId() {
        return rule.getRuleId();
    }

    public String toFormattedString(boolean printIds) {
        return toFormattedString(0, 4, printIds);
    }
    
    public String toFormattedString(int baseIndentation, int internalIndentation, boolean printIds) {
        
        String baseIndentString = Utils.fillWithSpaces(baseIndentation);
        String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
        StringBuffer sb = new StringBuffer();
        
        String effectString = rule.getEffect().toString().toLowerCase();
        
        if (printIds) {
            sb.append(String.format("%sid=%s\n", baseIndentString, rule.getRuleId()));
        }
        
        sb.append(String.format("%srule %s {\n", baseIndentString, effectString));
        
        for (AttributeWizard attributeWizard : targetWizard.getAttributeWizardList()) {
            sb.append(String.format("%s%s\n", indentString, attributeWizard.toFormattedString()));
        }
        
        sb.append(baseIndentString + "}");
        
        return sb.toString();
    }
}
