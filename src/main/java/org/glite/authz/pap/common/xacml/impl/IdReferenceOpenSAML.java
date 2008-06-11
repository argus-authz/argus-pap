package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.xacml.IdReference;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.w3c.dom.Node;

public class IdReferenceOpenSAML extends IdReference {
	
	private IdReferenceType idReference;
	private boolean isPolicyIdReference;

	public IdReferenceOpenSAML(IdReferenceType idReferenceType) {
		this.idReference = idReferenceType;
		if (idReferenceType.getElementQName().equals(IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME)) {
			isPolicyIdReference = true;
		} else {
			isPolicyIdReference = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public IdReferenceOpenSAML(Type type, String value) {
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		if (type == Type.POLICYSETIDREFERENCE) {
			XMLObjectBuilder<IdReferenceType> builder = builderFactory.getBuilder(org.opensaml.xacml.policy.IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME);
			idReference = builder.buildObject(org.opensaml.xacml.policy.IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME);
			isPolicyIdReference = false;
		} else {
			XMLObjectBuilder<IdReferenceType> builder = builderFactory.getBuilder(org.opensaml.xacml.policy.IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME);
			idReference = builder.buildObject(org.opensaml.xacml.policy.IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME);
			isPolicyIdReference = true;
		}
		idReference.setValue(value);
	}

	public Node getDOM() {
		return idReference.getDOM();
	}
	
	public IdReferenceType getOpenSAMLObject() {
		return idReference;
	}

	public String getValue() {
		return idReference.getValue();
	}

	public boolean isPolicyReference() {
		return isPolicyIdReference;
	}

	public boolean isPolicySetReference() {
		return !isPolicyIdReference;
	}
}