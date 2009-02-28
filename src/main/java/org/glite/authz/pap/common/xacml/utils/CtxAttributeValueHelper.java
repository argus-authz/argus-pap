package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.ctx.AttributeValueType;

public class CtxAttributeValueHelper extends XMLObjectHelper<AttributeValueType> {

    private static final javax.xml.namespace.QName elementQName = AttributeValueType.DEFAULT_ELEMENT_NAME;
    private static CtxAttributeValueHelper instance = new CtxAttributeValueHelper();

    private CtxAttributeValueHelper() {}

    public static AttributeValueType build(String value) {
        AttributeValueType attributeValue = (AttributeValueType) builderFactory.getBuilder(elementQName)
                .buildObject(elementQName);
        
        attributeValue.setValue(value);
        
        return attributeValue;
    }

    public static CtxAttributeValueHelper getInstance() {
        return instance;
    }

}
