package org.glite.authz.pap.common.xacml.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.EnvironmentMatchType;

public class EnvironmentMatchHelper extends XMLObjectHelper<EnvironmentMatchType> {

    private static final javax.xml.namespace.QName elementQName = EnvironmentMatchType.DEFAULT_ELEMENT_QNAME;
    private static EnvironmentMatchHelper instance = new EnvironmentMatchHelper();

    private EnvironmentMatchHelper() {}

    public static EnvironmentMatchType build() {
        return (EnvironmentMatchType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static EnvironmentMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId) {
        
        EnvironmentMatchType environmentMatch = build();
        
        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);
        
        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attribute
                .getDataType(), CtxAttributeTypeHelper.getFirstValue(attribute));
        
        environmentMatch.setEnvironmentAttributeDesignator(designator);
        environmentMatch.setAttributeValue(policyAttributeValue);
        
        return environmentMatch;
    }
    
    public static List<EnvironmentMatchType> buildWithDesignator(List<AttributeType> attributeList,
            String matchFunctionId) {
        
        List<EnvironmentMatchType> resultList = new ArrayList<EnvironmentMatchType>(attributeList.size());
        
        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId));
        }
        
        return resultList;
    }
    
    public static AttributeType getAttribute(EnvironmentMatchType environmentMatch) {

        AttributeValueType policyAttributeValue = environmentMatch.getAttributeValue();
        String attributeId = environmentMatch.getEnvironmentAttributeDesignator().getAttributeId();
        return CtxAttributeTypeHelper.build(attributeId, policyAttributeValue.getDataType(),
                policyAttributeValue.getValue());
        
    }

    public static List<AttributeType> getAttributeList(List<EnvironmentMatchType> environmentMatchList) {
        
        List<AttributeType> attributeList = new LinkedList<AttributeType>();
        
        if (environmentMatchList == null)
            return attributeList;
        
        for (EnvironmentMatchType subjectMatch:environmentMatchList) {
            attributeList.add(getAttribute(subjectMatch));
        }
        
        return attributeList;
        
    }

    public static EnvironmentMatchHelper getInstance() {
        return instance;
    }
}
