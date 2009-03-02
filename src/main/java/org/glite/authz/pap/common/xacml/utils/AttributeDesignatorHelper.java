package org.glite.authz.pap.common.xacml.utils;

import javax.xml.namespace.QName;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xml.Configuration;

public class AttributeDesignatorHelper extends XMLObjectHelper<AttributeDesignatorType> {
    
    private static final AttributeDesignatorHelper instance = new AttributeDesignatorHelper();

    public static AttributeDesignatorType build(QName designatorType, AttributeType attribute) {
        
        AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) Configuration
                .getBuilderFactory().getBuilder(designatorType).buildObject(designatorType);
        
        attributeDesignator.setAttribtueId(attribute.getAttributeID());
        attributeDesignator.setDataType(attribute.getDataType());
        
        return attributeDesignator;
    }

    public static AttributeDesignatorHelper getInstance() {
        return instance;
    }

    private AttributeDesignatorHelper() {}
    
}
