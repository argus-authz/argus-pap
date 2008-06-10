package org.glite.authz.pap.common.xacml.exceptions;

public class InvalidPolicySet extends XACMLException {

	private static final long serialVersionUID = 1L;

	public InvalidPolicySet() {
	}

	public InvalidPolicySet(String message) {
		super(message);
	}

	public InvalidPolicySet(Throwable cause) {
		super(cause);
	}

	public InvalidPolicySet(String message, Throwable cause) {
		super(message, cause);
	}

}
