package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.CtxAttributeValueHelper;
import org.glite.authz.pap.common.utils.xacml.DataType;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xml.Configuration;

public class WizardAttribute {
	
	public enum Type {
		SUBJECT_DN, SUBJECT_FQAN, SUBJECT_GENERIC_ATTRIBUTE, RESOURCE_RESOURCE_URI, RESOURCE_SERVICE_CLASS
	}

	private String attributeId;
	private String dataType;
	private String value;
	private boolean isSubjectAttribute = false;
	private boolean isResourceAttribute = false;
	private boolean isEnvironmentAttribute = false;
	
	public WizardAttribute(Type type, String value) {
		switch (type) {
		case SUBJECT_DN:
			attributeId = "DN";
			dataType = DataType.STRING;
			isSubjectAttribute = true;
			break;
		case SUBJECT_FQAN:
			attributeId = "FQAN";
			dataType = DataType.STRING;
			isSubjectAttribute = true;
			break;
		case SUBJECT_GENERIC_ATTRIBUTE:
			attributeId = "GA";
			dataType = DataType.STRING;
			isSubjectAttribute = true;
			break;
		case RESOURCE_RESOURCE_URI:
			attributeId = "RESOURCE_URI";
			dataType = DataType.STRING;
			isResourceAttribute= true;
			break;
		case RESOURCE_SERVICE_CLASS:
			attributeId = "SERVICE_CLASS";
			dataType = DataType.STRING;
			isResourceAttribute= true;
			break;
		}
		this.value = value;
	}
	
	public AttributeType getAttributeType() {
		AttributeType attribute = (AttributeType) Configuration
		.getBuilderFactory().getBuilder(
				AttributeType.DEFAULT_ELEMENT_NAME).buildObject(
				AttributeType.DEFAULT_ELEMENT_NAME);
		attribute.setAttributeID(attributeId);
		attribute.setDataType(dataType);
		attribute.getAttributeValues().add(CtxAttributeValueHelper.build(value));
		return attribute;
	}

	public boolean isSubjectAttribute() {
		return isSubjectAttribute;
	}

	public boolean isResourceAttribute() {
		return isResourceAttribute;
	}

	public boolean isEnvironmentAttribute() {
		return isEnvironmentAttribute;
	}
}
