package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.ActionsHelper;
import org.glite.authz.pap.common.utils.xacml.Functions;
import org.glite.authz.pap.common.utils.xacml.ResourceHelper;
import org.glite.authz.pap.common.utils.xacml.ResourceMatchHelper;
import org.glite.authz.pap.common.utils.xacml.ResourcesHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectMatchHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectsHelper;
import org.glite.authz.pap.common.utils.xacml.TargetHelper;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

public class TargetWizard {
    
    public static TargetType createTarget(List<AttributeWizard> targetAttributeWizardList) {
        
        List<AttributeType> sbjAttr = getSubjectAttributes(targetAttributeWizardList);
        List<AttributeType> rsrcAttr = getResourceAttributes(targetAttributeWizardList);

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(SubjectMatchHelper
                .buildListWithDesignator(sbjAttr, Functions.STRING_EQUAL)));

        ActionsType actions = ActionsHelper.buildAnyAction();
        

        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(ResourceMatchHelper
                .buildWithDesignator(rsrcAttr, Functions.STRING_EQUAL)));

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
    
    private static List<AttributeType> getSubjectAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isSubjectAttribute())
                resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }
    
    private static List<AttributeType> getResourceAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isResourceAttribute())
                resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }
    
    private static List<AttributeType> getEnvironmentAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isEnvironmentAttribute())
                resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }

}
