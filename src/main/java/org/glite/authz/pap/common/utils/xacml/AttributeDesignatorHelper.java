package org.glite.authz.pap.common.utils.xacml;

import javax.xml.namespace.QName;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xml.Configuration;

public class AttributeDesignatorHelper extends XACMLHelper<AttributeDesignatorType> {
	private static AttributeDesignatorHelper instance = null;

	public static AttributeDesignatorType build(QName designatorType, AttributeType attribute) {
		AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) Configuration
				.getBuilderFactory().getBuilder(designatorType).buildObject(designatorType);
		attributeDesignator.setAttribtueId(attribute.getAttributeID());
		attributeDesignator.setDataType(attribute.getDataType());
		return attributeDesignator;
	}

	public static AttributeDesignatorHelper getInstance() {
		if (instance == null) {
			instance = new AttributeDesignatorHelper();
		}
		return instance;
	}

	private AttributeDesignatorHelper() {
	}
}
