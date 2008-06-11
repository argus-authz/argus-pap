package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.xacml.IdReference;
import org.w3c.dom.Node;

public class IdReferenceImpl extends IdReference {
	private String value;
	private Node dom;
	
	private boolean isPolicyIdReference;
	
	public IdReferenceImpl(Type type, String value) {
		this(type, value, null);
	}
	
	public IdReferenceImpl(Type type, String value, Node dom) {
		this.dom = dom;
		this.value = value;
		if (type == Type.POLICYIDREFERENCE) {
			isPolicyIdReference = true;
		} else {
			isPolicyIdReference = false;
		}
	}

	public Node getDOM() {
		return dom;
	}

	public String getValue() {
		return value;
	}

	public boolean isPolicyReference() {
		return isPolicyIdReference;
	}

	public boolean isPolicySetReference() {
		return !isPolicyIdReference;
	}

}
