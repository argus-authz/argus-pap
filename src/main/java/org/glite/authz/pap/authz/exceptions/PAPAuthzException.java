package org.glite.authz.pap.authz.exceptions;

/**
 * 
 * Exception for authorization issues (ie, execution of
 * non authorized operation)
 *
 */
public class PAPAuthzException extends RuntimeException {

    /**
     * Serial version UID. 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public PAPAuthzException() {

    }


    /**
     * 
     * {@inheritDoc}
     */
    public PAPAuthzException( String message ) {

        super( message );

    }
    /**
     * 
     * {@inheritDoc}
     */
    public PAPAuthzException( Throwable cause ) {

        super( cause );

    }
    /**
     * 
     * {@inheritDoc}
     */
    public PAPAuthzException( String message, Throwable cause ) {

        super( message, cause );

    }

}
