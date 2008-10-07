package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.CtxAttributeTypeHelper;
import org.glite.authz.pap.common.utils.xacml.DataType;
import org.opensaml.xacml.ctx.AttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeWizard {

    public enum AttributeWizardType {
        DN("dn", "DN", DataType.STRING, TargetElement.SUBJECT),
        FQAN("fqan", "FQAN", DataType.STRING, TargetElement.SUBJECT),
        GA("ga", "GA", DataType.STRING, TargetElement.SUBJECT),
        RESOURCE_URI("resource_uri", "RESOURCE_URI", DataType.STRING, TargetElement.RESOURCE),
        SERVICE_CLASS("service_class", "SERVICE_CLASS", DataType.STRING, TargetElement.RESOURCE),
        DEFAULT("default", "DEFAULT", DataType.STRING, TargetElement.SUBJECT);

        public enum TargetElement {
            SUBJECT, ACTION, RESOURCE, ENVIRONMENT
        }

        private String id;
        private String xacmlId;
        private String dataType;
        private TargetElement targetElement;

        private AttributeWizardType(String id, String xacmlId, String dataType, TargetElement category) {
            this.id = id;
            this.xacmlId = xacmlId;
            this.dataType = dataType;
            this.targetElement = category;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(AttributeWizard.class);

    public static boolean isActionAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (AttributeWizardType supportedAttribute : AttributeWizardType.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.targetElement == AttributeWizardType.TargetElement.ACTION)
                    return true;
            }
        }
        return false;
    }

    public static boolean isEnvironmentAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (AttributeWizardType supportedAttribute : AttributeWizardType.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.targetElement == AttributeWizardType.TargetElement.ENVIRONMENT)
                    return true;
            }
        }
        return false;
    }

    public static boolean isResouceAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (AttributeWizardType supportedAttribute : AttributeWizardType.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.targetElement == AttributeWizardType.TargetElement.RESOURCE)
                    return true;
            }
        }
        return false;
    }

    public static boolean isSubjectAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        for (AttributeWizardType supportedAttribute : AttributeWizardType.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                if (supportedAttribute.targetElement == AttributeWizardType.TargetElement.SUBJECT)
                    return true;
            }
        }
        return false;
    }

    private AttributeWizardType attributeWizardType;
    private String value;

    public AttributeWizard(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        boolean notFound = true;
        for (AttributeWizardType supportedAttribute : AttributeWizardType.values()) {
            if (supportedAttribute.xacmlId.equals(xacmlId)) {
                attributeWizardType = supportedAttribute;
                this.value = CtxAttributeTypeHelper.getFirstValue(attribute);
                notFound = false;
            }
        }
        if (notFound)
            throw new UnsupportedAttributeException("Attribute not supported: " + xacmlId);
    }

    public AttributeWizard(String identifier, String value) {
        boolean found = false;
        for (AttributeWizardType supportedAttribute : AttributeWizardType.values()) {
            if (supportedAttribute.id.equals(identifier)) {
                attributeWizardType = supportedAttribute;
                this.value = value;
                found = true;
            }
        }
        if (!found) {
            log.warn("Unsupported attribute \"" + identifier
                    + "\". Assuming it to be a Generic Attribute");
            attributeWizardType = AttributeWizardType.DEFAULT;
            attributeWizardType.xacmlId = identifier;
            attributeWizardType.id = identifier;
            attributeWizardType.dataType = DataType.STRING;
            attributeWizardType.targetElement = AttributeWizardType.TargetElement.SUBJECT;
            this.value = value;
        }
    }

    public AttributeWizard(String idAndValue) {
        
        int separatorPosition =  idAndValue.indexOf('=');
        if (separatorPosition == -1)
            throw new UnsupportedAttributeException("'" + idAndValue + "' is not in the format 'id=value'");
        
        System.out.println("Evaluating: " + idAndValue);
        String identifier = idAndValue.substring(0, separatorPosition);
        String value = idAndValue.substring(separatorPosition + 1);

        boolean found = false;
        for (AttributeWizardType supportedAttribute : AttributeWizardType.values()) {
            if (supportedAttribute.id.equals(identifier)) {
                attributeWizardType = supportedAttribute;
                this.value = value;
                found = true;
            }
        }
        if (!found)
            throw new UnsupportedAttributeException("id=" + identifier);
    }

    public AttributeType getAttributeType() {
        return CtxAttributeTypeHelper.build(attributeWizardType.xacmlId, attributeWizardType.dataType,
                value);
    }

    public AttributeWizardType getAttributeWizardType() {
        return attributeWizardType;
    }

    public String getDataType() {
        return attributeWizardType.dataType;
    }

    public String getId() {
        return attributeWizardType.id;
    }

    public String getValue() {
        return value;
    }

    public String getXacmlId() {
        return attributeWizardType.xacmlId;
    }

    public boolean isActionAttribute() {
        if (AttributeWizardType.TargetElement.ACTION.equals(attributeWizardType.targetElement))
            return true;
        return false;
    }

    public boolean isEnvironmentAttribute() {
        if (AttributeWizardType.TargetElement.ENVIRONMENT.equals(attributeWizardType.targetElement))
            return true;
        return false;
    }

    public boolean isResourceAttribute() {
        if (AttributeWizardType.TargetElement.RESOURCE.equals(attributeWizardType.targetElement))
            return true;
        return false;
    }

    public boolean isSubjectAttribute() {
        if (AttributeWizardType.TargetElement.SUBJECT.equals(attributeWizardType.targetElement))
            return true;
        return false;
    }

    public String toFormattedString() {
        return getId() + "=" + getValue();
    }

}
