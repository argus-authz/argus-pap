package org.glite.authz.pap.ui.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.glite.authz.pap.common.utils.xacml.ActionsHelper;
import org.glite.authz.pap.common.utils.xacml.Functions;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.ResourceHelper;
import org.glite.authz.pap.common.utils.xacml.ResourceMatchHelper;
import org.glite.authz.pap.common.utils.xacml.ResourcesHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectMatchHelper;
import org.glite.authz.pap.common.utils.xacml.SubjectsHelper;
import org.glite.authz.pap.common.utils.xacml.TargetHelper;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

public abstract class PolicyWizard {

    private final static Random generator = new Random();
    private static TargetType createTarget(List<AttributeType> sbjAttr, List<AttributeType> rsrcAttr,
            List<AttributeType> envAttr) {

        SubjectsType subjects = SubjectsHelper.build(SubjectHelper.build(SubjectMatchHelper
                .buildListWithDesignator(sbjAttr, Functions.STRING_EQUAL)));

        ActionsType actions = ActionsHelper.buildAnyAction();

        ResourcesType resources = ResourcesHelper.build(ResourceHelper.build(ResourceMatchHelper
                .buildWithDesignator(rsrcAttr, Functions.STRING_EQUAL)));

        TargetType target = TargetHelper.build(subjects, actions, resources, null);

        return target;
    }

    private static List<AttributeType> getEnvironmentAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isEnvironmentAttribute())
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

    private static List<AttributeType> getSubjectAttributes(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (attribute.isSubjectAttribute())
                resultList.add(attribute.getAttributeType());
        }

        return resultList;
    }

    protected static long generateRandomLong() {
        return generator.nextLong();
    }

    protected final PolicyType policy;

    protected PolicyWizard(String policyId, List<AttributeWizard> targetAttributeList,
            List<List<AttributeWizard>> orExceptionsAttributeList, EffectType effect) {

        if (targetAttributeList == null)
            targetAttributeList = new LinkedList<AttributeWizard>();

        if (orExceptionsAttributeList == null)
            orExceptionsAttributeList = new LinkedList<List<AttributeWizard>>();

        policy = PolicyHelper.build(policyId, PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);

        // TODO: gestire list exceptions Or e AND
        List<AttributeWizard> exceptionsAttributeList = new LinkedList<AttributeWizard>();
        for (List<AttributeWizard> andList : orExceptionsAttributeList) {
            exceptionsAttributeList.addAll(andList);
        }

        TargetType target = createTarget(getSubjectAttributes(targetAttributeList),
                getResourceAttributes(targetAttributeList),
                getEnvironmentAttributes(targetAttributeList));

        RuleType rule = ExceptionsRule.build(getSubjectAttributes(exceptionsAttributeList),
                getResourceAttributes(exceptionsAttributeList),
                getEnvironmentAttributes(exceptionsAttributeList), effect);

        policy.setTarget(target);
        policy.getRules().add(rule);

    }

    public PolicyType getPolicyType() {
        return policy;
    }

    public String toString() {
        return PolicyHelper.getInstance().toString(policy);
    }

}
