package org.glite.authz.pap.common.exceptions;

public class XMLObjectParserException extends XMLObjectException {

    private static final long serialVersionUID = 1706754393978140235L;

    public XMLObjectParserException() {
    }

    public XMLObjectParserException(String message) {
        super(message);
    }

    public XMLObjectParserException(Throwable cause) {
        super(cause);
    }

    public XMLObjectParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
