package org.glite.authz.pap.common.xacml.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.ActionMatchType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeValueType;

public class ActionMatchHelper extends XMLObjectHelper<ActionMatchType> {

    private static final javax.xml.namespace.QName elementQName = ActionMatchType.DEFAULT_ELEMENT_NAME;
    private static ActionMatchHelper instance = new ActionMatchHelper();

    private ActionMatchHelper() {}

    public static ActionMatchType build() {
        return (ActionMatchType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ActionMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId) {
        
        if (attribute == null) {
            return null;
        }
        
        ActionMatchType actionMatch = build();
        
        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);
        
        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attribute
                .getDataType(), CtxAttributeTypeHelper.getFirstValue(attribute));
        
        actionMatch.setActionAttributeDesignator(designator);
        actionMatch.setAttributeValue(policyAttributeValue);
        actionMatch.setMatchId(matchFunctionId);
        
        return actionMatch;
    }
    
    public static List<ActionMatchType> buildWithDesignator(List<AttributeType> attributeList,
            String matchFunctionId) {
        
        List<ActionMatchType> resultList = new ArrayList<ActionMatchType>(attributeList.size());
        
        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId));
        }
        
        return resultList;
    }
    
    public static AttributeType getAttribute(ActionMatchType actionMatch) {

        AttributeValueType policyAttributeValue = actionMatch.getAttributeValue();
        String attributeId = actionMatch.getActionAttributeDesignator().getAttributeId();
        return CtxAttributeTypeHelper.build(attributeId, policyAttributeValue.getDataType(),
                policyAttributeValue.getValue());
        
    }

    public static List<AttributeType> getAttributeList(List<ActionMatchType> actionMatchList) {
        
        List<AttributeType> attributeList = new LinkedList<AttributeType>();
        
        if (actionMatchList == null) {
            return attributeList;
        }
        
        for (ActionMatchType subjectMatch:actionMatchList) {
            attributeList.add(getAttribute(subjectMatch));
        }
        return attributeList;
    }

    public static ActionMatchHelper getInstance() {
        return instance;
    }
}
