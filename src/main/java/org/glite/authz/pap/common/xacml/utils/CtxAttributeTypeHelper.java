package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.ctx.AttributeType;

public class CtxAttributeTypeHelper extends XMLObjectHelper<AttributeType> {

    private static final javax.xml.namespace.QName elementQName = AttributeType.DEFAULT_ELEMENT_NAME;
    private static CtxAttributeTypeHelper instance = new CtxAttributeTypeHelper();

    public static AttributeType build(String attributeId, String dataType, String value) {

        AttributeType attribute = (AttributeType) builderFactory.getBuilder(elementQName).buildObject(
                elementQName);

        attribute.setAttributeID(attributeId);
        attribute.setDataType(dataType);
        attribute.getAttributeValues().add(CtxAttributeValueHelper.build(value));

        return attribute;

    }
    
    public static String getFirstValue(AttributeType attribute) {
        
        org.opensaml.xacml.ctx.AttributeValueType ctxAttributeValue = (org.opensaml.xacml.ctx.AttributeValueType) attribute
        .getAttributeValues().get(0);
        
        return ctxAttributeValue.getValue();
        
    }

    public static CtxAttributeTypeHelper getInstance() {
        return instance;
    }

    private CtxAttributeTypeHelper() {}

}
