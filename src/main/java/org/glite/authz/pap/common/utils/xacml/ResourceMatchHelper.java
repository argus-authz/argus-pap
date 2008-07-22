package org.glite.authz.pap.common.utils.xacml;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ResourceMatchType;

public class ResourceMatchHelper extends XACMLHelper<ResourceMatchType> {

    private static final javax.xml.namespace.QName elementQName = ResourceMatchType.DEFAULT_ELEMENT_NAME;
    private static ResourceMatchHelper instance = new ResourceMatchHelper();

    public static ResourceMatchType build() {
        return (ResourceMatchType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static List<ResourceMatchType> buildWithDesignator(List<AttributeType> attributeList,
            String matchFunctionId) {
        
        List<ResourceMatchType> resultList = new ArrayList<ResourceMatchType>(attributeList.size());
        
        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId));
        }
        
        return resultList;
    }

    public static ResourceMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId) {
        
        ResourceMatchType resourceMatch = build();
        
        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);
        
        org.opensaml.xacml.ctx.AttributeValueType ctxAttributeValue = (org.opensaml.xacml.ctx.AttributeValueType) attribute
                .getAttributeValues().get(0);
        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attribute
                .getDataType(), ctxAttributeValue.getValue());
        
        resourceMatch.setResourceAttributeDesignator(designator);
        resourceMatch.setAttributeValue(policyAttributeValue);
        
        return resourceMatch;
    }

    public static ResourceMatchHelper getInstance() {
        return instance;
    }

    private ResourceMatchHelper() {}

}
