package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.utils.ActionsHelper;
import org.glite.authz.pap.common.xacml.utils.Functions;
import org.glite.authz.pap.common.xacml.utils.ResourceHelper;
import org.glite.authz.pap.common.xacml.utils.ResourceMatchHelper;
import org.glite.authz.pap.common.xacml.utils.ResourcesHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectMatchHelper;
import org.glite.authz.pap.common.xacml.utils.SubjectsHelper;
import org.glite.authz.pap.common.xacml.utils.TargetHelper;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

public class TargetWizard {
    
    public static TargetType buildTarget(List<AttributeWizard> targetAttributeWizardList) {
        
        if (targetAttributeWizardList == null) {
            targetAttributeWizardList = new ArrayList<AttributeWizard>(0);
        }
        
        List<AttributeType> sbjAttr = getSubjectAttributes(targetAttributeWizardList);
        List<AttributeType> rsrcAttr = getResourceAttributes(targetAttributeWizardList);
        List<AttributeType> envAttr = getEnvironmentAttributes(targetAttributeWizardList);
        List<AttributeType> actionAttr = getActions(targetAttributeWizardList);
        
        if (sbjAttr.size() + rsrcAttr.size() + envAttr.size() + actionAttr.size() != targetAttributeWizardList.size())
            throw new WizardException("BUG: error building the Targert");

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(SubjectMatchHelper
                .buildListWithDesignator(sbjAttr, Functions.STRING_EQUAL)));

        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(ResourceMatchHelper
                .buildWithDesignator(rsrcAttr, Functions.STRING_EQUAL)));
        
        
        ActionsType actions = ActionsHelper.buildAnyAction();
        


        TargetType target = TargetHelper.build(subjects, actions, resources, null);

        return target;
    }
    
    public static List<AttributeWizard> extractTargetAttributeWizardList(TargetType target) {

        List<AttributeWizard> attributeWizardList = new LinkedList<AttributeWizard>();
        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        List<SubjectType> subjectList = target.getSubjects().getSubjects();

        if (!subjectList.isEmpty()) {

            if (subjectList.size() > 1)
                throw new UnsupportedPolicyException("Policy has more than one Subject");

            attributeList.addAll(SubjectMatchHelper.getAttributeList(subjectList.get(0)
                    .getSubjectMatches()));
        }

        List<ResourceType> resourceList = target.getResources().getResources();

        if (!resourceList.isEmpty()) {

            if (resourceList.size() > 1)
                throw new UnsupportedPolicyException("Policy has more than one Resource");

            attributeList.addAll(ResourceMatchHelper.getAttributeList(resourceList.get(0)
                    .getResourceMatches()));
        }

        for (AttributeType attribute : attributeList) {
            attributeWizardList.add(new AttributeWizard(attribute));
        }

        return attributeWizardList;
    }

    private static List<AttributeType> getActions(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isActionAttribute()) {
                resultList.add(attribute.getAttributeType());
            }
        }

        return resultList;
    }
    
    private static List<AttributeType> getEnvironmentAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isEnvironmentAttribute()) {
                resultList.add(attribute.getAttributeType());
            }
        }

        return resultList;
    }
    
    private static List<AttributeType> getResourceAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isResourceAttribute()) {
                resultList.add(attribute.getAttributeType());
            }
        }

        return resultList;
    }
    
    private static List<AttributeType> getSubjectAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isSubjectAttribute()) {
                resultList.add(attribute.getAttributeType());
            }
        }

        return resultList;
    }
}
