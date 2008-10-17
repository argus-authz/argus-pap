package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.CtxAttributeTypeHelper;
import org.glite.authz.pap.common.utils.xacml.DataType;
import org.glite.authz.pap.ui.wizard.AttributeWizard.AttributeWizardType.TargetElement;
import org.opensaml.xacml.ctx.AttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeWizard {
	
    public enum AttributeWizardType {
        DEFAULT("default", "DEFAULT", DataType.STRING, TargetElement.SUBJECT),
        DN("dn", "DN", DataType.STRING, TargetElement.SUBJECT),
        FQAN("fqan", "FQAN", DataType.STRING, TargetElement.SUBJECT),
        GA("ga", "GA", DataType.STRING, TargetElement.SUBJECT),
        RESOURCE_URI("resource_uri", "RESOURCE_URI", DataType.STRING, TargetElement.RESOURCE),
        SERVICE_CLASS("service_class", "SERVICE_CLASS", DataType.STRING, TargetElement.RESOURCE);

        public enum TargetElement {
            ACTION, ENVIRONMENT, RESOURCE, SUBJECT
        }

        public static AttributeWizardType getById(String id) {
        	for (AttributeWizardType awt:AttributeWizardType.values()) {
        		if (awt.id.equals(id))
        			return awt;
        	}
        	return null;
        }
        public static AttributeWizardType getByXACMLId(String xacmlId) {
        	for (AttributeWizardType awt:AttributeWizardType.values()) {
        		if (awt.xacmlId.equals(xacmlId))
        			return awt;
        	}
        	return null;
        }
        public static boolean idExist(String id) {
        	for (AttributeWizardType awt:AttributeWizardType.values()) {
        		if (awt.id.equals(id))
        			return true;
        	}
        	return false;
        }
        public static boolean xacmlIdExist(String id) {
        	for (AttributeWizardType awt:AttributeWizardType.values()) {
        		if (awt.xacmlId.equals(id))
        			return true;
        	}
        	return false;
        }

        public static boolean xacmlIdMatchesTargetElement(String xacmlId, TargetElement targetElement) {
        	for (AttributeWizardType awt:AttributeWizardType.values()) {
        		if (awt.xacmlId.equals(xacmlId)) {
        			if (awt.targetElement.equals(targetElement))
        				return true;
        		}
        	}
        	return false;
        }
        
        private String dataType;
        private String id;
        private TargetElement targetElement;
        private String xacmlId;
        
        private AttributeWizardType(String id, String xacmlId, String dataType, TargetElement category) {
            this.id = id;
            this.xacmlId = xacmlId;
            this.dataType = dataType;
            this.targetElement = category;
        }
        
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AttributeWizard.class);

    public static boolean isActionAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardType.xacmlIdMatchesTargetElement(xacmlId, TargetElement.ACTION);
    }

    public static boolean isEnvironmentAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardType.xacmlIdMatchesTargetElement(xacmlId, TargetElement.ENVIRONMENT);
    }

    public static boolean isResouceAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardType.xacmlIdMatchesTargetElement(xacmlId, TargetElement.RESOURCE);
    }

    public static boolean isSubjectAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardType.xacmlIdMatchesTargetElement(xacmlId, TargetElement.SUBJECT);
    }

    private static String getIdFromIdEqualValue(String idEqualValue) {
    	int separatorPosition =  getSeparatorPositionForIdEqualValue(idEqualValue);
        return idEqualValue.substring(0, separatorPosition);
    }
    private static int getSeparatorPositionForIdEqualValue(String idEqualValue) {
    	int separatorPosition =  idEqualValue.indexOf('=');
        if (separatorPosition == -1)
            throw new UnsupportedAttributeException("'" + idEqualValue + "' is not in the format 'id=value'");
        return separatorPosition;
    }

    private static String getValueFromIdEqualValue(String idEqualValue) {
    	int separatorPosition =  getSeparatorPositionForIdEqualValue(idEqualValue);
    	
        String value = idEqualValue.substring(separatorPosition + 1);
        
        if (value.startsWith("\""))
            value = value.substring(1);
        
        if (value.endsWith("\""))
            value = value.substring(0, value.length());
        
        return value;
    }
    
    private AttributeWizardType attributeWizardType;
    private String value;

    public AttributeWizard(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        attributeWizardType = AttributeWizardType.getByXACMLId(xacmlId);
        if (attributeWizardType == null)
        	throw new UnsupportedAttributeException("Attribute not supported: " + xacmlId);
        this.value = CtxAttributeTypeHelper.getFirstValue(attribute);
    }

    public AttributeWizard(AttributeWizardType attributeWizardType, String value) {
        this.attributeWizardType = attributeWizardType;
        this.value = value;
    }

    public AttributeWizard(String idEqualValue) {
    	this(getIdFromIdEqualValue(idEqualValue), getValueFromIdEqualValue(idEqualValue));
    }

    public AttributeWizard(String identifier, String value) {
    	attributeWizardType = AttributeWizardType.getById(identifier);
    	if (attributeWizardType == null)
    		throw new UnsupportedAttributeException("id=" + identifier);
    	this.value = value;
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
        return getId() + "=\"" + getValue() + "\"";
    }

}
