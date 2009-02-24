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
    private final List<AttributeWizard> targetAttributeWizardList;
    
    public RuleWizard(EffectType effect) {
        this(null, effect);
    }
    
    public RuleWizard(List<AttributeWizard> targetAttributeWizardList, EffectType effect) {
        
        rule = RuleHelper.build(WizardUtils.generateId("Rule"), effect);
        
        if (targetAttributeWizardList == null) {
            targetAttributeWizardList = new ArrayList<AttributeWizard>(0);
        }
        
        this.targetAttributeWizardList = targetAttributeWizardList;
        
        
        if (targetAttributeWizardList.size() == 0) {
            return;
        }
        
        TargetWizard targetWizard = new TargetWizard(targetAttributeWizardList);

        rule.setTarget(targetWizard.getXACML());
    }
    
    public RuleWizard(RuleType rule) {
        
        if (rule == null) {
            throw new WizardException("Invalid argument: RuleType is null.");
        }
        
        this.rule = rule;
        
        TargetWizard targetWizard = new TargetWizard(rule.getTarget());
        
        this.targetAttributeWizardList = targetWizard.getAttributeWizardList();
    }
    
    public List<AttributeWizard> getAttributeWizardList() {
        return targetAttributeWizardList;
    }
    
    public RuleType getXACML() {
        return rule;
    }
    
    public String toFormattedString(boolean printIds) {
        return toFormattedString(0, 4, printIds);
    }

    public String toFormattedString(int baseIndentation, int internalIndentation, boolean printIds) {
        
        String baseIndentString = Utils.fillWithSpaces(baseIndentation);
        String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
        StringBuffer sb = new StringBuffer();
        
        String effectString = rule.getEffect().toString();
        
        if (printIds) {
            sb.append(String.format("%sid=%s\n", baseIndentString, rule.getRuleId()));
        }
        
        sb.append(String.format("%srule %s {\n", baseIndentString, effectString));
        
        for (AttributeWizard attributeWizard : targetAttributeWizardList) {
            sb.append(String.format("%s%s\n", indentString, attributeWizard.toFormattedString()));
        }
        
        sb.append(baseIndentString + "}");
        
        return sb.toString();
    }
}
