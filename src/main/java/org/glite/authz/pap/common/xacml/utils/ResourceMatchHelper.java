package org.glite.authz.pap.common.xacml.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ResourceMatchType;

public class ResourceMatchHelper extends XMLObjectHelper<ResourceMatchType> {

    private static final javax.xml.namespace.QName elementQName = ResourceMatchType.DEFAULT_ELEMENT_NAME;
    private static ResourceMatchHelper instance = new ResourceMatchHelper();

    private ResourceMatchHelper() {}

    public static ResourceMatchType build() {
        return (ResourceMatchType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ResourceMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId) {

        if (attribute == null) {
            return null;
        }

        ResourceMatchType resourceMatch = build();

        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);

        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attribute.getDataType(),
                CtxAttributeTypeHelper.getFirstValue(attribute));

        resourceMatch.setResourceAttributeDesignator(designator);
        resourceMatch.setAttributeValue(policyAttributeValue);

        return resourceMatch;
    }

    public static List<ResourceMatchType> buildWithDesignator(List<AttributeType> attributeList, String matchFunctionId) {

        List<ResourceMatchType> resultList = new ArrayList<ResourceMatchType>(attributeList.size());

        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId));
        }

        return resultList;
    }

    public static AttributeType getAttribute(ResourceMatchType resourceMatch) {

        AttributeValueType policyAttributeValue = resourceMatch.getAttributeValue();
        String attributeId = resourceMatch.getResourceAttributeDesignator().getAttributeId();
        return CtxAttributeTypeHelper.build(attributeId, policyAttributeValue.getDataType(), policyAttributeValue.getValue());

    }

    public static List<AttributeType> getAttributeList(List<ResourceMatchType> resourceMatchList) {

        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        if (resourceMatchList == null) {
            return attributeList;
        }

        for (ResourceMatchType subjectMatch : resourceMatchList) {
            attributeList.add(getAttribute(subjectMatch));
        }

        return attributeList;

    }

    public static ResourceMatchHelper getInstance() {
        return instance;
    }

}
