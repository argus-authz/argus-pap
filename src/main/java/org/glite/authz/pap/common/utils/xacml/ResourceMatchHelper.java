package org.glite.authz.pap.common.utils.xacml;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ResourceMatchType;
import org.opensaml.xml.Configuration;

public class ResourceMatchHelper extends XACMLHelper<ResourceMatchType> {
    private static ResourceMatchHelper instance = null;

    public static ResourceMatchType build() {
	return (ResourceMatchType) Configuration.getBuilderFactory()
		.getBuilder(ResourceMatchType.DEFAULT_ELEMENT_NAME)
		.buildObject(ResourceMatchType.DEFAULT_ELEMENT_NAME);
    }

    public static ResourceMatchType buildWithDesignator(
	    AttributeType attribute, String matchFunctionId) {
	ResourceMatchType resourceMatch = build();
	AttributeDesignatorType designator = AttributeDesignatorHelper
		.build(
			AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME,
			attribute);

	resourceMatch.setResourceAttributeDesignator(designator);
	org.opensaml.xacml.ctx.AttributeValueType ctxAttributeValue = (org.opensaml.xacml.ctx.AttributeValueType) attribute
		.getAttributeValues().get(0);
	AttributeValueType policyAttributeValue = PolicyAttributeValueHelper
		.build(attribute.getDataType(), ctxAttributeValue.getValue());
	resourceMatch.setAttributeValue(policyAttributeValue);
	return resourceMatch;
    }

    public static List<ResourceMatchType> buildListWithDesignator(
	    List<AttributeType> attributeList, String matchFunctionId) {
	List<ResourceMatchType> resultList = new ArrayList<ResourceMatchType>(
		attributeList.size());
	for (AttributeType attribute : attributeList) {
	    resultList.add(buildWithDesignator(attribute, matchFunctionId));
	}
	return resultList;
    }

    public static ResourceMatchHelper getInstance() {
	if (instance == null) {
	    instance = new ResourceMatchHelper();
	}
	return instance;
    }

    private ResourceMatchHelper() {
    }

}
