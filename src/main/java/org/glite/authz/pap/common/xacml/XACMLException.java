package org.glite.authz.pap.common.xacml;

public class XACMLException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public XACMLException() {
	}

	public XACMLException(String message) {
		super(message);
	}

	public XACMLException(Throwable cause) {
		super(cause);
	}

	public XACMLException(String message, Throwable cause) {
		super(message, cause);
	}

}