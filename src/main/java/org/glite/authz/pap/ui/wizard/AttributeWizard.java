package org.glite.authz.pap.ui.wizard;

import org.glite.authz.pap.common.utils.xacml.CtxAttributeValueHelper;
import org.glite.authz.pap.common.utils.xacml.DataType;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xml.Configuration;

public class AttributeWizard {
	
	private enum Attribute {
		DN("dn", "DN", DataType.STRING, Category.SUBJECT),
		FQAN("fqan", "FQAN", DataType.STRING, Category.SUBJECT),
		GA("ga", "GA", DataType.STRING, Category.SUBJECT),
		RESOURCE_URI("resource_uri", "RESOURCE_URI", DataType.STRING, Category.RESOURCE),
		SERVICE_CLASS("service_class", "SERVICE_CLASS", DataType.STRING, Category.RESOURCE);
		//PILOT
		
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
	
	private enum Category {
		SUBJECT, ACTION, RESOURCE, ENVIRONMENT
	}
	
	private String attributeId;
	private String dataType;
	private String value;
	private boolean isSubjectAttribute = false;
	private boolean isResourceAttribute = false;
	private boolean isEnvironmentAttribute = false;
	private boolean isActionAttribute = false;
	
	public AttributeWizard(String identifier, String value) throws UnsupportedAttributeException {
		boolean found = false;
		for (Attribute attr:Attribute.values()) {
			if (attr.id.equals(identifier)) {
				this.attributeId = attr.xacmlId;
				this.dataType = attr.dataType;
				this.value = value;
				switch (attr.category) {
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
			throw new UnsupportedAttributeException();
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
	
}
