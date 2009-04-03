package org.glite.authz.pap.authz.exceptions;

import org.glite.authz.pap.common.exceptions.PAPException;

/**
 * 
 * Exception for authorization configuration problems.
 * 
 */
public class PAPAuthzConfigurationException extends PAPException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    public PAPAuthzConfigurationException() {

        super();

    }

    /**
     * {@inheritDoc}
     */
    public PAPAuthzConfigurationException( String message, Throwable cause ) {

        super( message, cause );

    }

    /**
     * {@inheritDoc}
     */
    public PAPAuthzConfigurationException( String message ) {

        super( message );

    }

    /**
     * {@inheritDoc}
     */
    public PAPAuthzConfigurationException( Throwable cause ) {

        super( cause );

    }

}
