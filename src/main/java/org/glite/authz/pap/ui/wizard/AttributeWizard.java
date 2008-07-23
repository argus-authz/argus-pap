package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.CtxAttributeValueHelper;
import org.glite.authz.pap.common.utils.xacml.DataType;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xml.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeWizard {
    
    private static final Logger log = LoggerFactory.getLogger(AttributeWizard.class);

    private enum Attribute {
        DN("dn", "DN", DataType.STRING, Category.SUBJECT),
        FQAN("fqan", "FQAN", DataType.STRING, Category.SUBJECT),
        GA("ga", "GA", DataType.STRING, Category.SUBJECT),
        RESOURCE_URI("resource_uri", "RESOURCE_URI", DataType.STRING, Category.RESOURCE),
        SERVICE_CLASS("service_class", "SERVICE_CLASS", DataType.STRING, Category.RESOURCE);

        private enum Category {
            SUBJECT, ACTION, RESOURCE, ENVIRONMENT
        }

        private final String id;
        private final String xacmlId;
        private final String dataType;
        private final Category category;

        private Attribute(String id, String xacmlId, String dataType, Category category) {
            this.id = id;
            this.xacmlId = xacmlId;
            this.dataType = dataType;
            this.category = category;
        }
    }

    private String attributeId;
    private String dataType;
    private String value;
    private boolean isSubjectAttribute = false;
    private boolean isResourceAttribute = false;
    private boolean isEnvironmentAttribute = false;
    private boolean isActionAttribute = false;

    public AttributeWizard(String identifier, String value) {
        boolean found = false;
        for (Attribute supportedAttribute : Attribute.values()) {
            if (supportedAttribute.id.equals(identifier)) {
                this.attributeId = supportedAttribute.xacmlId;
                this.dataType = supportedAttribute.dataType;
                this.value = value;
                switch (supportedAttribute.category) {
                case SUBJECT:
                    isSubjectAttribute = true;
                    break;
                case ACTION:
                    isActionAttribute = true;
                    break;
                case RESOURCE:
                    isResourceAttribute = true;
                    break;
                case ENVIRONMENT:
                    isEnvironmentAttribute = true;
                    break;
                }
                found = true;
            }
        }
        if (!found) {
            log.warn("Unsupported attribute \"" + identifier + "\". Assuming it to be a Generic Attribute");
            this.attributeId = identifier;
            this.dataType = DataType.STRING;
            this.value = value;
            isSubjectAttribute = true;
        }
    }

    public AttributeType getAttributeType() {

        AttributeType attribute = (AttributeType) Configuration.getBuilderFactory().getBuilder(
                AttributeType.DEFAULT_ELEMENT_NAME).buildObject(AttributeType.DEFAULT_ELEMENT_NAME);

        attribute.setAttributeID(attributeId);
        attribute.setDataType(dataType);
        attribute.getAttributeValues().add(CtxAttributeValueHelper.build(value));

        return attribute;
    }

    public boolean isActionAttribute() {
        return isActionAttribute;
    }

    public boolean isEnvironmentAttribute() {
        return isEnvironmentAttribute;
    }

    public boolean isResourceAttribute() {
        return isResourceAttribute;
    }

    public boolean isSubjectAttribute() {
        return isSubjectAttribute;
    }
    
    public static boolean isSubjectAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (Attribute supportedAttribute : Attribute.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.category == Attribute.Category.SUBJECT)
                    return true;
            }
        }
        return false;
    }
    
    public static boolean isResouceAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (Attribute supportedAttribute : Attribute.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.category == Attribute.Category.RESOURCE)
                    return true;
            }
        }
        return false;
    }
    
    public static boolean isEnvironmentAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (Attribute supportedAttribute : Attribute.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.category == Attribute.Category.ENVIRONMENT)
                    return true;
            }
        }
        return false;
    }
    
    public static boolean isActionAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (Attribute supportedAttribute : Attribute.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.category == Attribute.Category.ACTION)
                    return true;
            }
        }
        return false;
    }

}
