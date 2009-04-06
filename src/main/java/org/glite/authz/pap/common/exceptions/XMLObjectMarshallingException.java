package org.glite.authz.pap.common.exceptions;

public class XMLObjectMarshallingException extends XMLObjectException {

    private static final long serialVersionUID = -7268709514539060454L;

    public XMLObjectMarshallingException() {
    }

    public XMLObjectMarshallingException(String message) {
	super(message);
    }

    public XMLObjectMarshallingException(Throwable cause) {
	super(cause);
    }

    public XMLObjectMarshallingException(String message, Throwable cause) {
	super(message, cause);
    }

}
