package org.glite.authz.pap.common.exceptions;

public class XMLObjectException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XMLObjectException() {
    }

    public XMLObjectException(String message) {
	super(message);
    }

    public XMLObjectException(Throwable cause) {
	super(cause);
    }

    public XMLObjectException(String message, Throwable cause) {
	super(message, cause);
    }

}
