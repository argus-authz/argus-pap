package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.ctx.AttributeValueType;

public class CtxAttributeValueHelper extends XACMLHelper<AttributeValueType> {

    private static final javax.xml.namespace.QName elementQName = AttributeValueType.DEFAULT_ELEMENT_NAME;
    private static CtxAttributeValueHelper instance = new CtxAttributeValueHelper();

    public static AttributeValueType build(String value) {
        AttributeValueType attributeValue = (AttributeValueType) builderFactory.getBuilder(elementQName)
                .buildObject(elementQName);
        
        attributeValue.setValue(value);
        
        return attributeValue;
    }

    public static CtxAttributeValueHelper getInstance() {
        return instance;
    }

    private CtxAttributeValueHelper() {}

}
