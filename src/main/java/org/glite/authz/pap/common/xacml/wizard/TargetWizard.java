package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.utils.ActionHelper;
import org.glite.authz.pap.common.xacml.utils.ActionMatchHelper;
import org.glite.authz.pap.common.xacml.utils.ActionsHelper;
import org.glite.authz.pap.common.xacml.utils.Functions;
import org.glite.authz.pap.common.xacml.utils.ResourceHelper;
import org.glite.authz.pap.common.xacml.utils.ResourceMatchHelper;
import org.glite.authz.pap.common.xacml.utils.ResourcesHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectMatchHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectsHelper;
import org.glite.authz.pap.common.xacml.utils.TargetHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.TargetWizardException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EnvironmentsType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

public class TargetWizard {
    
    private final TargetType target;
    private final List<AttributeWizard> targetAttributeWizardList;
    
    
    public TargetWizard(TargetType target) {
        this.target = target;
        this.targetAttributeWizardList = buildAttributeWizardList(target);
    }
    
    public TargetWizard(List<AttributeWizard> targetAttributeWizardList) {
        
        if (targetAttributeWizardList == null) {
            targetAttributeWizardList = new ArrayList<AttributeWizard>(0);
        }
        
        this.targetAttributeWizardList = targetAttributeWizardList;

        List<AttributeType> sbjAttr = WizardUtils.getSubjectAttributes(targetAttributeWizardList);
        List<AttributeType> rsrcAttr = WizardUtils.getResourceAttributes(targetAttributeWizardList);
        List<AttributeType> envAttr = WizardUtils.getEnvironmentAttributes(targetAttributeWizardList);
        List<AttributeType> actionAttr = WizardUtils.getActions(targetAttributeWizardList);

        if (sbjAttr.size() + rsrcAttr.size() + envAttr.size() + actionAttr.size() != targetAttributeWizardList.size())
            throw new TargetWizardException("BUG: error building the Target");

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(SubjectMatchHelper.buildListWithDesignator(sbjAttr,
                Functions.STRING_EQUAL)));

        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(ResourceMatchHelper.buildWithDesignator(rsrcAttr,
                Functions.STRING_EQUAL)));

        ActionsType actions = ActionsHelper.build(ActionHelper.build(ActionMatchHelper.buildWithDesignator(actionAttr,
                Functions.STRING_EQUAL)));

        EnvironmentsType environments = null;
        
        target = TargetHelper.build(subjects, actions, resources, environments);

    }

    public TargetType getXACML() {
        return target;
    }
    
    public List<AttributeWizard> getAttributeWizardList() {
        return targetAttributeWizardList;
    }

    private static List<AttributeWizard> buildAttributeWizardList(TargetType target) {

        List<AttributeWizard> attributeWizardList = new LinkedList<AttributeWizard>();
        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        List<SubjectType> subjectList = target.getSubjects().getSubjects();

        if (!subjectList.isEmpty()) {

            if (subjectList.size() > 1)
                throw new UnsupportedPolicyException("Policy has more than one Subject");

            attributeList.addAll(SubjectMatchHelper.getAttributeList(subjectList.get(0).getSubjectMatches()));
        }

        List<ResourceType> resourceList = target.getResources().getResources();

        if (!resourceList.isEmpty()) {

            if (resourceList.size() > 1)
                throw new UnsupportedPolicyException("Policy has more than one Resource");

            attributeList.addAll(ResourceMatchHelper.getAttributeList(resourceList.get(0).getResourceMatches()));
        }

        for (AttributeType attribute : attributeList) {
            attributeWizardList.add(new AttributeWizard(attribute));
        }

        return attributeWizardList;
    }
    
    public boolean equals(Object targetWizardObject) {
        
        if (!(targetWizardObject instanceof TargetWizard)) {
            return false;
        }
        
        TargetWizard targetWizard = (TargetWizard) targetWizardObject;
        
        List<AttributeWizard> attributeWizardList = targetWizard.getAttributeWizardList();
        if (targetAttributeWizardList.size() != attributeWizardList.size()) {
            return false;
        }
        
        for (AttributeWizard thisAttributeWizard : targetAttributeWizardList) {
            
            boolean found = false;
            
            for (AttributeWizard attributeWizard : attributeWizardList) {
                if (thisAttributeWizard.equals(attributeWizard)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
