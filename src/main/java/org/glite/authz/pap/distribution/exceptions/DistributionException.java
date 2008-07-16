package org.glite.authz.pap.distribution.exceptions;

public class DistributionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DistributionException() {
    }

    public DistributionException(String message) {
	super(message);
    }

    public DistributionException(Throwable cause) {
	super(cause);
    }

    public DistributionException(String message, Throwable cause) {
	super(message, cause);
    }

}
