package org.glite.authz.pap.common.exceptions;

public class FileNotFoundXACMLException extends XACMLException {

    private static final long serialVersionUID = 1L;

    public FileNotFoundXACMLException() {
    }

    public FileNotFoundXACMLException(String message) {
	super(message);
    }

    public FileNotFoundXACMLException(Throwable cause) {
	super(cause);
    }

    public FileNotFoundXACMLException(String message, Throwable cause) {
	super(message, cause);
    }

}
