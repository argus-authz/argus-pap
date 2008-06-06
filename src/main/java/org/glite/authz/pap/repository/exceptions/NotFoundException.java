package org.glite.authz.pap.repository.exceptions;

public class NotFoundException extends RepositoryException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

}
