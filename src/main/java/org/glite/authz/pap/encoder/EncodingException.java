package org.glite.authz.pap.encoder;

/**
 * This class is the exception raised by
 * {@link org.glite.authz.pap.encoder.PolicyFileEncoder}
 * when there are parsing problems.
 *
 * It acts as an adaptor of other errors.
 *
 * @author Vincenzo Ciaschini
 */
public class EncodingException extends Exception {
    /**
     * This method converts a {@code Throwable} object to
     * an EncodingException object.
     *
     * @param cause The {@code Throwable} object to convert.
     */
    public EncodingException(Throwable cause) {
        super(cause);
    }

    /**
     * This method creates a basic exception.
     */
    public EncodingException() {
        super();
    }

    /**
     * This method raises an EncodingException object with a specified message.
     *
     * @param message The message to add.
     */
    public EncodingException(String message) {
        super(message);
    }

    /**
     * This method raises an EncodingException object with a specified message
     * and to encapsulate the specified Throwable object.
     *
     * @param cause The {@code Throwable} object to convert.
     * @param message The message to add.
     */
    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
