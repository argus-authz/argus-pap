package org.glite.authz.pap.papmanagement;


public class AliasNotFoundException extends DistributionConfigurationException {

	private static final long serialVersionUID = 1L;

	public AliasNotFoundException() {
	}

	public AliasNotFoundException(String message) {
		super(message);
	}

	public AliasNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AliasNotFoundException(Throwable cause) {
		super(cause);
	}

}
