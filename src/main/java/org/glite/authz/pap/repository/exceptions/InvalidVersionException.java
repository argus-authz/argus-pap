package org.glite.authz.pap.repository.exceptions;

public class InvalidVersionException extends RepositoryException {

	private static final long serialVersionUID = 2993186414977277136L;

	public InvalidVersionException() {
	}

	public InvalidVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidVersionException(String message) {
		super(message);
	}

	public InvalidVersionException(Throwable cause) {
		super(cause);
	}

}
