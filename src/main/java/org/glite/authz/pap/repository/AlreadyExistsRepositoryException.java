package org.glite.authz.pap.repository;

public class AlreadyExistsRepositoryException extends RepositoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyExistsRepositoryException() {
	}

	public AlreadyExistsRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyExistsRepositoryException(String message) {
		super(message);
	}

	public AlreadyExistsRepositoryException(Throwable cause) {
		super(cause);
	}

}
