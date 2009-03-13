package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.policy.AttributeAssignmentType;

public class AttributeAssignmentHelper extends XMLObjectHelper<AttributeAssignmentType> {

    private static final javax.xml.namespace.QName elementQName = AttributeAssignmentType.DEFAULT_ELEMENT_NAME;
    private static final AttributeAssignmentHelper instance = new AttributeAssignmentHelper();

    private AttributeAssignmentHelper() {}

    public static AttributeAssignmentType build(String attributeId, String value, String dataType) {
        AttributeAssignmentType attributeAssignment = (AttributeAssignmentType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        attributeAssignment.setAttributeId(attributeId);
        attributeAssignment.setValue(value);
        attributeAssignment.setDataType(dataType);
        return attributeAssignment;
    }
    
    public static AttributeAssignmentHelper getInstance() {
        return instance;
    }

}
