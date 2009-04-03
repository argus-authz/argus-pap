package org.glite.authz.pap.authz;

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import javax.servlet.ServletRequest;

import org.glite.authz.pap.authz.exceptions.PAPAuthzException;
import org.glite.security.SecurityContext;
import org.glite.security.util.DN;
import org.glite.security.util.DNHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is responsible for properly initializing the security context as a
 * consequence of client's request. If no valid X509 certificate chain is found
 * in the request context, a {@link PAPAuthzException} is thrown
 * 
 */
public class InitSecurityContext {

    /** The index string for the remote address as stored in the security context **/
    static public final String SECURITY_CONTEXT_REMOTE_ADDRESS = "org.glite.authz.pap.remote_address";

    static Logger logger = LoggerFactory.getLogger( InitSecurityContext.class );

    /**
     * Initializes the context from a servlet request.
     * @param request
     */
    public static void setContextFromRequest( final ServletRequest request ) {

        SecurityContext sc = new SecurityContext();
        SecurityContext.setCurrentContext( sc );

        String remoteAddress = request.getRemoteAddr();
        sc.setProperty( SECURITY_CONTEXT_REMOTE_ADDRESS, remoteAddress );

        X509Certificate[] certChain = null;
        try {

            certChain = (X509Certificate[]) request
                    .getAttribute( "javax.servlet.request.X509Certificate" );

        } catch ( Exception e ) {

            logger.error( "Exception during certificate chain retrieval: " + e );
            throw new PAPAuthzException( "No certificate found in request!", e );

        }
        
        if (certChain == null)
            throw new PAPAuthzException( "No certificate found in request!"); 

        sc.setClientCertChain( certChain );

        DN subject = DNHandler.getSubject( sc.getClientCert() );
        DN issuer = DNHandler.getIssuer( sc.getClientCert() );

        BigInteger sn = sc.getClientCert().getSerialNumber();
        String serialNumber = ( sn == null ) ? "NULL" : sn.toString();

        if ( sc.getClientName() != null )
            sc.setClientName( subject.getX500() );

        if ( sc.getIssuerName() != null )
            sc.setIssuerName( issuer.getX500() );

        logger.info( "Connection from \"" + remoteAddress + "\" by \""
                + sc.getClientName() + "\" (issued by \"" + sc.getIssuerName()
                + "\", " + "serial " + serialNumber + ")" );

    }

}
