package org.glite.authz.pap.common.exceptions;

/**
 * Base exception for PAP.
 * 
 * 
 */
public class PAPException extends RuntimeException {

    /**
     * Static version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public PAPException() {

        super();
    
    }

    /**
     * {@inheritDoc}
     */
    public PAPException( String message, Throwable cause ) {

        super( message, cause );
    }

    /**
     * {@inheritDoc}
     */
    public PAPException( String message ) {
        super( message );
    }

    /**
     * {@inheritDoc}
     */
    public PAPException( Throwable cause ) {

        super( cause );
    }

}
