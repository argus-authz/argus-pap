package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.policy.AttributeValueType;

public class PolicyAttributeValueHelper extends XMLObjectHelper<AttributeValueType> {

    private static final javax.xml.namespace.QName elementQName = AttributeValueType.DEFAULT_ELEMENT_NAME;
    private static PolicyAttributeValueHelper instance = new PolicyAttributeValueHelper();

    private PolicyAttributeValueHelper() {}

    public static AttributeValueType build(String type, String value) {

        AttributeValueType attributeValue = (AttributeValueType) builderFactory.getBuilder(elementQName)
                .buildObject(elementQName);

        attributeValue.setDataType(type);
        attributeValue.setValue(value);

        return attributeValue;
    }

    public static PolicyAttributeValueHelper getInstance() {
        return instance;
    }

}
