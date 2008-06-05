package org.glite.authz.pap.encoder;

public class EncodingException extends Exception {
    public EncodingException(Throwable cause) {
        super(cause);
    }

    public EncodingException() {
        super();
    }

    public EncodingException(String message) {
        super(message);
    }

    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
