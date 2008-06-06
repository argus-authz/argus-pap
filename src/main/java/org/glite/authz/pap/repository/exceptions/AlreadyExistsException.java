package org.glite.authz.pap.repository.exceptions;

public class AlreadyExistsException extends RepositoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyExistsException() {
	}

	public AlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyExistsException(String message) {
		super(message);
	}

	public AlreadyExistsException(Throwable cause) {
		super(cause);
	}

}
