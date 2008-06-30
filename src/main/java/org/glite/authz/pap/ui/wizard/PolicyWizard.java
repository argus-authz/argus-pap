package org.glite.authz.pap.ui.wizard;

import java.util.LinkedList;
import java.util.List;

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
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

public class PolicyWizard {

	public static PolicyType build(String policyId, List<AttributeWizard> targetAttributeList,
			List<AttributeWizard> exceptionsAttributeList, EffectType effect) {
		
		PolicyType policy = PolicyHelper.buildWithAnyTarget(policyId,
				PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);
		
		List<AttributeType> subjectAttributes = new LinkedList<AttributeType>();
		List<AttributeType> resourceAttributes = new LinkedList<AttributeType>();
		List<AttributeType> environmentAttributes = new LinkedList<AttributeType>();
		divideIntoSubLists(targetAttributeList, subjectAttributes, resourceAttributes, environmentAttributes);
		
		policy.setTarget(createTarget(subjectAttributes, resourceAttributes, environmentAttributes));
		subjectAttributes.clear();
		resourceAttributes.clear();
		environmentAttributes.clear();
		divideIntoSubLists(exceptionsAttributeList, subjectAttributes, resourceAttributes, environmentAttributes);
		policy.getRules().add(ExceptionsRule.build(subjectAttributes, resourceAttributes, environmentAttributes, effect));

		return policy;
	}

	private static TargetType createTarget(List<AttributeType> sbjAttr,
			List<AttributeType> rsrcAttr, List<AttributeType> envAttr) {

		SubjectsType subjects = SubjectsHelper.build(SubjectHelper
				.build(SubjectMatchHelper.buildListWithDesignator(sbjAttr,
						Functions.STRING_EQUAL)));
		ActionsType actions = ActionsHelper.buildAnyAction();

		ResourcesType resources = ResourcesHelper.build(ResourceHelper
				.build(ResourceMatchHelper.buildListWithDesignator(rsrcAttr,
						Functions.STRING_EQUAL)));

		TargetType target = TargetHelper.build(subjects, actions, resources,
				null);
		return target;
	}

	private static void divideIntoSubLists(List<AttributeWizard> list,
			List<AttributeType> subjectAttributes,
			List<AttributeType> resourceAttributes,
			List<AttributeType> environmentAttributes) {
		for (AttributeWizard entry : list) {
			AttributeType attribute = entry.getAttributeType();
			if (entry.isSubjectAttribute()) {
				subjectAttributes.add(attribute);
			} else if (entry.isResourceAttribute()) {
				resourceAttributes.add(attribute);
			} else if (entry.isEnvironmentAttribute()) {
				environmentAttributes.add(attribute);
			}
		}
	}

	private PolicyWizard() {
	}
}
