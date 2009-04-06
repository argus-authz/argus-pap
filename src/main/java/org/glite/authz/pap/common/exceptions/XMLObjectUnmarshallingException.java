package org.glite.authz.pap.common.exceptions;

public class XMLObjectUnmarshallingException extends XMLObjectException {

    private static final long serialVersionUID = -7268709514539060454L;

    public XMLObjectUnmarshallingException() {
    }

    public XMLObjectUnmarshallingException(String message) {
	super(message);
    }

    public XMLObjectUnmarshallingException(Throwable cause) {
	super(cause);
    }

    public XMLObjectUnmarshallingException(String message, Throwable cause) {
	super(message, cause);
    }

}
