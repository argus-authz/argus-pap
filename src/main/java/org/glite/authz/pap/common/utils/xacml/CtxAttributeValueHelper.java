package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xml.Configuration;

public class CtxAttributeValueHelper extends XACMLHelper<AttributeValueType> {

    private static CtxAttributeValueHelper instance = null;

    public static AttributeValueType build(String value) {
	AttributeValueType attributeValue = (AttributeValueType) Configuration
		.getBuilderFactory().getBuilder(
			AttributeValueType.DEFAULT_ELEMENT_NAME).buildObject(
			AttributeValueType.DEFAULT_ELEMENT_NAME);
	attributeValue.setValue(value);
	return attributeValue;
    }

    public static CtxAttributeValueHelper getInstance() {
	if (instance == null) {
	    instance = new CtxAttributeValueHelper();
	}
	return instance;
    }

    private CtxAttributeValueHelper() {
    }

}
