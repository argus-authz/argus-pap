package org.glite.authz.pap.ui.wizard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.glite.authz.pap.common.utils.xacml.ApplyHelper;
import org.glite.authz.pap.common.utils.xacml.AttributeDesignatorHelper;
import org.glite.authz.pap.common.utils.xacml.PolicyAttributeValueHelper;
import org.glite.authz.pap.common.utils.xacml.FunctionHelper;
import org.glite.authz.pap.common.utils.xacml.Functions;
import org.glite.authz.pap.common.utils.xacml.RuleHelper;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.policy.ApplyType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.FunctionType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;

public class ExceptionsRule {

    public static RuleType build(List<AttributeType> sbjAttr,
	    List<AttributeType> rsrcAttr, List<AttributeType> envAttr,
	    EffectType effect) {

	ConditionType condition = (ConditionType) Configuration
		.getBuilderFactory().getBuilder(
			ConditionType.DEFAULT_ELEMENT_NAME).buildObject(
			ConditionType.DEFAULT_ELEMENT_NAME);
	ApplyType applyNot = ApplyHelper.buildFunctionNot();
	ApplyType applyOr = ApplyHelper.buildFunctionOr();
	applyNot.getExpressions().add(applyOr);

	condition.setExpression(applyNot);

	RuleType exceptionsRule = RuleHelper.build("ExceptionsRule", effect);
	exceptionsRule.setCondition(condition);

	List<List<AttributeType>> listOfLists = getSubLists(sbjAttr);
	for (List<AttributeType> list : listOfLists) {
	    applyOr
		    .getExpressions()
		    .add(
			    createAnyOfAll(
				    list,
				    AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME));
	}

	listOfLists = getSubLists(rsrcAttr);
	for (List<AttributeType> list : listOfLists) {
	    applyOr
		    .getExpressions()
		    .add(
			    createAnyOfAll(
				    list,
				    AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME));
	}

	listOfLists = getSubLists(envAttr);
	for (List<AttributeType> list : listOfLists) {
	    applyOr
		    .getExpressions()
		    .add(
			    createAnyOfAll(
				    list,
				    AttributeDesignatorType.ENVIRONMENT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME));
	}

	return exceptionsRule;
    }

    private static List<List<AttributeType>> getSubLists(
	    List<AttributeType> attributeList) {
	List<List<AttributeType>> resultList = new ArrayList<List<AttributeType>>();
	if (attributeList == null) {
	    return resultList;
	}
	Set<String> idSet = new HashSet<String>();
	for (AttributeType attribute : attributeList) {
	    idSet.add(attribute.getAttributeID());
	}
	for (String id : idSet) {
	    List<AttributeType> list = new LinkedList<AttributeType>();
	    for (AttributeType attribute : attributeList) {
		if (attribute.getAttributeID().equals(id)) {
		    list.add(attribute);
		}
	    }
	    resultList.add(list);
	}
	return resultList;
    }

    private static ApplyType createAnyOfAll(List<AttributeType> attributeList,
	    QName designatorType) {

	ApplyType applyAnyOfAll = ApplyHelper.buildFunctionAnyOfAll();
	FunctionType functionStringEqual = FunctionHelper
		.build(Functions.STRING_EQUAL);
	applyAnyOfAll.getExpressions().add(functionStringEqual);

	ApplyType applyStringBag = ApplyHelper.buildFunctionStringBag();
	if (!attributeList.isEmpty()) {
	    for (AttributeType attribute : attributeList) {
		String dataType = attribute.getDataType();
		for (XMLObject elem : attribute.getAttributeValues()) {
		    applyStringBag.getExpressions().add(
			    PolicyAttributeValueHelper.build(dataType,
				    ((AttributeValueType) elem).getValue()));
		}
	    }
	    applyAnyOfAll.getExpressions().add(applyStringBag);
	    applyAnyOfAll.getExpressions().add(
		    AttributeDesignatorHelper.build(designatorType,
			    attributeList.get(0)));
	}
	return applyAnyOfAll;
    }

}
