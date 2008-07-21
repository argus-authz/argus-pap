package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xml.Configuration;

public class PolicyAttributeValueHelper extends XACMLHelper<AttributeValueType> {

    private static PolicyAttributeValueHelper instance = null;

    public static AttributeValueType build(String type, String value) {
	AttributeValueType attributeValue = (AttributeValueType) Configuration
		.getBuilderFactory().getBuilder(
			AttributeValueType.DEFAULT_ELEMENT_NAME).buildObject(
			AttributeValueType.DEFAULT_ELEMENT_NAME);
	attributeValue.setDataType(type);
	attributeValue.setValue(value);
	return attributeValue;
    }

    public static PolicyAttributeValueHelper getInstance() {
	if (instance == null) {
	    instance = new PolicyAttributeValueHelper();
	}
	return instance;
    }

    private PolicyAttributeValueHelper() {
    }

}
