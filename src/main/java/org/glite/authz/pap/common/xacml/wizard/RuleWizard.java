package org.glite.authz.pap.common.xacml.wizard;

import java.util.List;

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
    
    public RuleWizard(RuleType rule) {
        
        if (rule == null) {
            throw new WizardException("Invalid argument: RuleType is null.");
        }
        
        this.rule = rule;
        
        TargetWizard targetWizard = new TargetWizard(rule.getTarget());
        
        this.targetAttributeWizardList = targetWizard.getAttributeWizardList();
    }
    
    public RuleWizard(List<AttributeWizard> targetAttributeWizardList, EffectType effect) {
        
        rule = RuleHelper.build(WizardUtils.generateId(null), effect);
        
        this.targetAttributeWizardList = targetAttributeWizardList;
        
        if (targetAttributeWizardList == null) {
            return;
        }
        
        if (targetAttributeWizardList.size() == 0) {
            return;
        }
        
        TargetWizard targetWizard = new TargetWizard(targetAttributeWizardList);

        rule.setTarget(targetWizard.getXACML());
    }
    
    public RuleType getXACML() {
        return rule;
    }
    
    public List<AttributeWizard> getAttributeWizardList() {
        return targetAttributeWizardList;
    }
}
