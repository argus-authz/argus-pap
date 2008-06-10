package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.IdReference.Type;
import org.w3c.dom.Node;

public class ReferenceIdImpl implements IdReference {
	private String value;
	private Node dom;
	
	private boolean isPolicyIdReference;
	
	public ReferenceIdImpl(Type type, String value) {
		this(type, value, null);
	}
	
	public ReferenceIdImpl(Type type, String value, Node dom) {
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

	public boolean isPolicy() {
		return false;
	}

	public boolean isPolicyReference() {
		return isPolicyIdReference;
	}

	public boolean isPolicySet() {
		return false;
	}

	public boolean isPolicySetReference() {
		return !isPolicyIdReference;
	}

	public boolean isReference() {
		return true;
	}

}
