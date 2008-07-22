package org.glite.authz.pap.ui.wizard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.glite.authz.pap.common.utils.xacml.ApplyHelper;
import org.glite.authz.pap.common.utils.xacml.AttributeDesignatorHelper;
import org.glite.authz.pap.common.utils.xacml.FunctionHelper;
import org.glite.authz.pap.common.utils.xacml.Functions;
import org.glite.authz.pap.common.utils.xacml.PolicyAttributeValueHelper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionsRule {

    private static final Logger log = LoggerFactory.getLogger(ExceptionsRule.class);
    private static final javax.xml.namespace.QName SUBJECT_DESIGNATOR = AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME;
    private static final javax.xml.namespace.QName RESOURCE_DESIGNATOR = AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME;
    private static final javax.xml.namespace.QName ENVIRONMENT_DESIGNATOR = AttributeDesignatorType.ENVIRONMENT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME;

    public static RuleType build(List<AttributeType> sbjAttr, List<AttributeType> rsrcAttr,
            List<AttributeType> envAttr, EffectType effect) {

        ApplyType applyNot = ApplyHelper.buildFunctionNot();
        ApplyType applyOr = ApplyHelper.buildFunctionOr();
        applyNot.getExpressions().add(applyOr);

        List<List<AttributeType>> listOfLists = getSubLists(sbjAttr);

        for (List<AttributeType> list : listOfLists) {
            applyOr.getExpressions().add(createFunctionAnyOfANY(list, SUBJECT_DESIGNATOR));
        }

        listOfLists = getSubLists(rsrcAttr);
        for (List<AttributeType> list : listOfLists) {
            applyOr.getExpressions().add(createFunctionAnyOfANY(list, RESOURCE_DESIGNATOR));
        }

        listOfLists = getSubLists(envAttr);
        for (List<AttributeType> list : listOfLists) {
            applyOr.getExpressions().add(createFunctionAnyOfANY(list, ENVIRONMENT_DESIGNATOR));
        }

        ConditionType condition = (ConditionType) Configuration.getBuilderFactory().getBuilder(
                ConditionType.DEFAULT_ELEMENT_NAME).buildObject(ConditionType.DEFAULT_ELEMENT_NAME);
        condition.setExpression(applyNot);

        RuleType exceptionsRule = RuleHelper.build("ExceptionsRule", effect);
        exceptionsRule.setCondition(condition);

        return exceptionsRule;
    }

    public static RuleType build(List<List<AttributeType>> orAttributeList, EffectType effect) {

        ApplyType applyOr = ApplyHelper.buildFunctionOr();

        for (List<AttributeType> andAttributeList : orAttributeList) {

            if (andAttributeList.isEmpty())
                continue;

            if (andAttributeList.size() == 1) {
                AttributeType attribute = andAttributeList.get(0);
                applyOr.getExpressions().add(createFunctionAnyOf(attribute, getDesignator(attribute)));
            } else {
                ApplyType applyAnd = ApplyHelper.buildFunctionAnd();
                for (AttributeType attribute : andAttributeList) {
                    applyAnd.getExpressions().add(
                            createFunctionAnyOf(attribute, getDesignator(attribute)));
                }
                applyOr.getExpressions().add(applyAnd);
            }

        }

        ApplyType applyNot = ApplyHelper.buildFunctionNot();
        applyNot.getExpressions().add(applyOr);

        ConditionType condition = (ConditionType) Configuration.getBuilderFactory().getBuilder(
                ConditionType.DEFAULT_ELEMENT_NAME).buildObject(ConditionType.DEFAULT_ELEMENT_NAME);
        condition.setExpression(applyNot);

        RuleType exceptionsRule = RuleHelper.build("ExceptionsRule", effect);
        exceptionsRule.setCondition(condition);

        return exceptionsRule;
    }

    public static RuleType buildo(List<AttributeType> attributeList, EffectType effect) {

        ApplyType applyNot = ApplyHelper.buildFunctionNot();
        ApplyType applyOr = ApplyHelper.buildFunctionOr();
        applyNot.getExpressions().add(applyOr);

        for (AttributeType attribute : attributeList) {
            applyOr.getExpressions().add(createFunctionAnyOf(attribute, getDesignator(attribute)));
        }

        ConditionType condition = (ConditionType) Configuration.getBuilderFactory().getBuilder(
                ConditionType.DEFAULT_ELEMENT_NAME).buildObject(ConditionType.DEFAULT_ELEMENT_NAME);
        condition.setExpression(applyNot);

        RuleType exceptionsRule = RuleHelper.build("ExceptionsRule", effect);
        exceptionsRule.setCondition(condition);

        return exceptionsRule;
    }

    private static ApplyType createFunctionAnyOf(AttributeType attribute, QName designatorType) {

        FunctionType functionStringEqual = FunctionHelper.build(Functions.STRING_EQUAL);

        if (attribute.getAttributeValues().size() != 1)
            return null;

        AttributeValueType attributeValue = (AttributeValueType) attribute.getAttributeValues().get(0);

        org.opensaml.xacml.policy.AttributeValueType policyAttributeValue;
        policyAttributeValue = PolicyAttributeValueHelper.build(attribute.getDataType(), attributeValue
                .getValue());

        ApplyType applyAnyOf = ApplyHelper.buildFunctionAnyOf();

        applyAnyOf.getExpressions().add(functionStringEqual);
        applyAnyOf.getExpressions().add(policyAttributeValue);
        applyAnyOf.getExpressions().add(AttributeDesignatorHelper.build(designatorType, attribute));

        return applyAnyOf;
    }

    private static ApplyType createFunctionAnyOfANY(List<AttributeType> attributeList,
            QName designatorType) {

        ApplyType applyAnyOfAll = ApplyHelper.buildFunctionAnyOfAny();
        FunctionType functionStringEqual = FunctionHelper.build(Functions.STRING_EQUAL);
        applyAnyOfAll.getExpressions().add(functionStringEqual);

        ApplyType applyStringBag = ApplyHelper.buildFunctionStringBag();
        if (!attributeList.isEmpty()) {
            for (AttributeType attribute : attributeList) {
                String dataType = attribute.getDataType();
                for (XMLObject elem : attribute.getAttributeValues()) {
                    applyStringBag.getExpressions().add(
                            PolicyAttributeValueHelper.build(dataType, ((AttributeValueType) elem)
                                    .getValue()));
                }
            }
            applyAnyOfAll.getExpressions().add(applyStringBag);
            applyAnyOfAll.getExpressions().add(
                    AttributeDesignatorHelper.build(designatorType, attributeList.get(0)));
        }
        return applyAnyOfAll;
    }

    private static javax.xml.namespace.QName getDesignator(AttributeType attribute) {

        if (AttributeWizard.isSubjectAttribute(attribute))
            return SUBJECT_DESIGNATOR;
        else if (AttributeWizard.isResouceAttribute(attribute))
            return RESOURCE_DESIGNATOR;
        else if (AttributeWizard.isEnvironmentAttribute(attribute))
            return ENVIRONMENT_DESIGNATOR;
        else {
            log.error("BUG subject designator assigned by default: attributeId="
                    + attribute.getAttributeID() + " dataType=" + attribute.getDataType());
            return SUBJECT_DESIGNATOR;
        }

    }

    private static List<List<AttributeType>> getSubLists(List<AttributeType> attributeList) {
        List<List<AttributeType>> resultList = new ArrayList<List<AttributeType>>();

        if (attributeList == null)
            return resultList;

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

}
