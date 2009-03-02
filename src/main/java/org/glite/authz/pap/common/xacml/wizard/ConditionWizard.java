package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.glite.authz.pap.common.xacml.utils.ApplyHelper;
import org.glite.authz.pap.common.xacml.utils.AttributeDesignatorHelper;
import org.glite.authz.pap.common.xacml.utils.CtxAttributeTypeHelper;
import org.glite.authz.pap.common.xacml.utils.FunctionHelper;
import org.glite.authz.pap.common.xacml.utils.Functions;
import org.glite.authz.pap.common.xacml.utils.PolicyAttributeValueHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.policy.ApplyType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.ExpressionType;
import org.opensaml.xacml.policy.FunctionType;
import org.opensaml.xml.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionWizard {
    
    private static final javax.xml.namespace.QName ENVIRONMENT_DESIGNATOR = AttributeDesignatorType.ENVIRONMENT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME;
    private static final Logger log = LoggerFactory.getLogger(ConditionWizard.class);
    private static final javax.xml.namespace.QName RESOURCE_DESIGNATOR = AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME;
    private static final javax.xml.namespace.QName SUBJECT_DESIGNATOR = AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME;
    
    public static List<List<AttributeWizard>> getAttributeWizardListList(ConditionType condition) {

        List<List<AttributeWizard>> resultList = new LinkedList<List<AttributeWizard>>();

        if (condition == null)
            throw new UnsupportedPolicyException("Condition not found");

        ExpressionType expression = condition.getExpression();
        if (expression == null)
            throw new UnsupportedPolicyException("Wrong \"Expression\" in \"Condition\"");

        ApplyType apply;

        if (expression instanceof ApplyType) {
            apply = (ApplyType) expression;
        } else {
            throw new UnsupportedPolicyException("First expression is not an Apply NOT()");
        }

        if (!Functions.NOT.equals(apply.getFunctionId()))
            throw new UnsupportedPolicyException("First expression is not an Apply NOT()");

        if (apply.getExpressions().size() != 1)
            throw new UnsupportedPolicyException("Second expression is not a single Apply OR()");

        expression = apply.getExpressions().get(0);
        if (expression instanceof ApplyType) {
            apply = (ApplyType) expression;
        } else {
            throw new UnsupportedPolicyException("Second expression is not an Apply OR()");
        }

        if (!Functions.OR.equals(apply.getFunctionId()))
            throw new UnsupportedPolicyException("Second expression is not an Apply OR()");

        List<ExpressionType> expressionList = apply.getExpressions();

        for (int i = 0; i < expressionList.size(); i++) {
            expression = expressionList.get(i);

            if (!(expression instanceof ApplyType))
                throw new UnsupportedPolicyException("Wrong expression inside the Apply OR()");
            apply = (ApplyType) expression;

            List<AttributeWizard> andList = new LinkedList<AttributeWizard>();
            if (Functions.ANY_OF.equals(apply.getFunctionId())) {
                AttributeType attribute = getAttributeFromApplyAnyOf(apply);
                andList.add(new AttributeWizard(attribute));
            } else if (Functions.AND.equals(apply.getFunctionId())) {
                for (AttributeType attribute : getAttributeFromApplyAnd(apply)) {
                    andList.add(new AttributeWizard(attribute));
                }
            } else
                throw new UnsupportedPolicyException("Wrong function inside the Apply OR()");
            resultList.add(andList);
        }

        return resultList;
    }
    
    public static ConditionType getXACML(List<List<AttributeWizard>> orExceptionsAttributeWizardList) {
        List<List<AttributeType>> orAttributeList = getAttributeTypeListList(orExceptionsAttributeWizardList);
        
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
        
        return condition;
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
    
    private static List<AttributeType> getAttributeFromApplyAnd(ApplyType apply) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();
        for (ExpressionType expression : apply.getExpressions()) {
            if (!(expression instanceof ApplyType))
                throw new UnsupportedPolicyException("Wrong expression inside the Apply AND()");

            apply = (ApplyType) expression;

            if (!Functions.ANY_OF.equals(apply.getFunctionId()))
                throw new UnsupportedPolicyException("Wrong function inside the Apply AND()");
            resultList.add(getAttributeFromApplyAnyOf(apply));
        }
        return resultList;
    }
    
    private static AttributeType getAttributeFromApplyAnyOf(ApplyType apply) {

        if (apply.getExpressions().size() != 3)
            throw new UnsupportedPolicyException("Wrong number of expressions inside the Apply ANY-OF");

        ExpressionType expression = apply.getExpressions().get(0);
        if (!(expression instanceof FunctionType))
            throw new UnsupportedPolicyException("First Expression of Apply ANY-OF is not a function (STRING-EQUAL)");

        if (!Functions.STRING_EQUAL.equals(((FunctionType) expression).getFunctionId()))
            throw new UnsupportedPolicyException("First Expression of Apply ANY-OF is not a function (STRING-EQUAL)");

        expression = apply.getExpressions().get(1);
        if (!(expression instanceof org.opensaml.xacml.policy.AttributeValueType))
            throw new UnsupportedPolicyException("Second Expression of Apply ANY-OF is not an AttributeValueType");

        String value = ((org.opensaml.xacml.policy.AttributeValueType) expression).getValue();

        expression = apply.getExpressions().get(2);
        if (!(expression instanceof AttributeDesignatorType))
            throw new UnsupportedPolicyException("Second Expression of Apply ANY-OF is not an AttributeDesignator");

        String dataType = ((AttributeDesignatorType) expression).getDataType();
        String xacmlId = ((AttributeDesignatorType) expression).getAttributeId();

        return CtxAttributeTypeHelper.build(xacmlId, dataType, value);
    }
    
    private static List<AttributeType> getAttributeTypeList(List<AttributeWizard> list) {
        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            resultList.add(attribute.getXACML());
        }

        return resultList;
    }
    
    private static List<List<AttributeType>> getAttributeTypeListList(
            List<List<AttributeWizard>> listList) {
        List<List<AttributeType>> resultList = new LinkedList<List<AttributeType>>();

        for (List<AttributeWizard> list : listList) {
            resultList.add(getAttributeTypeList(list));
        }

        return resultList;
    }
    
    private static javax.xml.namespace.QName getDesignator(AttributeType attribute) {

        if (AttributeWizard.isSubjectAttribute(attribute))
            return SUBJECT_DESIGNATOR;
        else if (AttributeWizard.isResouceAttribute(attribute))
            return RESOURCE_DESIGNATOR;
        else if (AttributeWizard.isEnvironmentAttribute(attribute))
            return ENVIRONMENT_DESIGNATOR;
        else {
            log.warn("Subject designator assigned by default: attributeId=" + attribute.getAttributeID()
                    + " dataType=" + attribute.getDataType());
            return SUBJECT_DESIGNATOR;
        }

    }
    

}
