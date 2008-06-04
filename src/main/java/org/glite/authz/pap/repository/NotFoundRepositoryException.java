package org.glite.authz.pap.repository;

public class NotFoundRepositoryException extends RepositoryException {

	private static final long serialVersionUID = 1L;

	public NotFoundRepositoryException() {
	}

	public NotFoundRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundRepositoryException(String message) {
		super(message);
	}

	public NotFoundRepositoryException(Throwable cause) {
		super(cause);
	}

}
