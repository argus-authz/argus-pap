package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.utils.ActionHelper;
import org.glite.authz.pap.common.xacml.utils.ActionMatchHelper;
import org.glite.authz.pap.common.xacml.utils.ActionsHelper;
import org.glite.authz.pap.common.xacml.utils.EnvironmentHelper;
import org.glite.authz.pap.common.xacml.utils.EnvironmentMatchHelper;
import org.glite.authz.pap.common.xacml.utils.EnvironmentsHelper;
import org.glite.authz.pap.common.xacml.utils.Functions;
import org.glite.authz.pap.common.xacml.utils.ResourceHelper;
import org.glite.authz.pap.common.xacml.utils.ResourceMatchHelper;
import org.glite.authz.pap.common.xacml.utils.ResourcesHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectMatchHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectsHelper;
import org.glite.authz.pap.common.xacml.utils.TargetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.common.xacml.wizard.exceptions.TargetWizardException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionType;
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

    public TargetWizard(AttributeWizard attributeWizard) {
        
        targetAttributeWizardList = new ArrayList<AttributeWizard>(1);
        targetAttributeWizardList.add(attributeWizard);

        target = createTarget(targetAttributeWizardList);
    }
    
    public TargetWizard(List<AttributeWizard> targetAttributeWizardList) {

        if (targetAttributeWizardList == null) {
            targetAttributeWizardList = new ArrayList<AttributeWizard>(0);
        }

        this.targetAttributeWizardList = targetAttributeWizardList;
        target = createTarget(targetAttributeWizardList);
    }

    public TargetWizard(TargetType target) {
        this.target = target;
        this.targetAttributeWizardList = buildAttributeWizardList(target);
    }
    
    private static List<AttributeWizard> buildAttributeWizardList(TargetType target) {

        List<AttributeWizard> attributeWizardList = new LinkedList<AttributeWizard>();
        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        // get subjects
        List<SubjectType> subjectList = target.getSubjects().getSubjects();

        if (!subjectList.isEmpty()) {

            if (subjectList.size() > 1) {
                throw new UnsupportedPolicyException("Only one SubjectType is allowed");
            }

            attributeList.addAll(SubjectMatchHelper.getAttributeList(subjectList.get(0).getSubjectMatches()));
        }

        // get resources
        List<ResourceType> resourceList = target.getResources().getResources();

        if (!resourceList.isEmpty()) {

            if (resourceList.size() > 1) {
                throw new UnsupportedPolicyException("Only one ResourceSubjectType is allowed");
            }

            attributeList.addAll(ResourceMatchHelper.getAttributeList(resourceList.get(0).getResourceMatches()));
        }

        // get actions
        List<ActionType> actionList = target.getActions().getActions();

        if (!actionList.isEmpty()) {

            if (actionList.size() > 1) {
                throw new UnsupportedPolicyException("Only one ActionSubjectType is allowed");
            }

            attributeList.addAll(ActionMatchHelper.getAttributeList(actionList.get(0).getActionMatches()));
        }

        // build AttributeWizard list
        for (AttributeType attribute : attributeList) {
            attributeWizardList.add(new AttributeWizard(attribute));
        }

        return attributeWizardList;
    }
    
    private static TargetType createTarget(List<AttributeWizard> targetAttributeWizardList) {
        List<AttributeType> sbjAttr = WizardUtils.getAttributes(targetAttributeWizardList,
                AttributeWizardType.TargetElement.SUBJECT);
        List<AttributeType> rsrcAttr = WizardUtils.getAttributes(targetAttributeWizardList,
                AttributeWizardType.TargetElement.RESOURCE);
        List<AttributeType> envAttr = WizardUtils.getAttributes(targetAttributeWizardList,
                AttributeWizardType.TargetElement.ENVIRONMENT);
        List<AttributeType> actionAttr = WizardUtils.getAttributes(targetAttributeWizardList,
                AttributeWizardType.TargetElement.ACTION);

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(SubjectMatchHelper.buildListWithDesignator(sbjAttr,
                Functions.STRING_EQUAL)));
        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(ResourceMatchHelper.buildWithDesignator(rsrcAttr,
                Functions.STRING_EQUAL)));
        ActionsType actions = ActionsHelper.build(ActionHelper.build(ActionMatchHelper.buildWithDesignator(actionAttr,
                Functions.STRING_EQUAL)));
        EnvironmentsType environments = EnvironmentsHelper.build(EnvironmentHelper.build(EnvironmentMatchHelper.buildWithDesignator(envAttr,
                Functions.STRING_EQUAL)));

        TargetType target = TargetHelper.build(subjects, actions, resources, environments);
        
        return target;
    }
    
    public List<AttributeWizard> getAttributeWizardList() {
        return targetAttributeWizardList;
    }

    public TargetType getXACML() {
        return target;
    }

    public boolean isEqual(TargetType target) {
        
        TargetWizard targetWizard;
        
        try {
            targetWizard = new TargetWizard(target);
        } catch (TargetWizardException e) {
            return false;
        }
        
        return isEqual(targetWizard);
    }

    public boolean isEqual(TargetWizard targetWizard) {

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
