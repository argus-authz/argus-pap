package org.glite.authz.pap.server.jetty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.net.ssl.SSLServerSocketFactory;

import org.glite.security.trustmanager.ContextWrapper;
import org.mortbay.jetty.security.SslSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrustManagerSocketConnector extends SslSocketConnector {
    
    private static final Logger log = LoggerFactory
    .getLogger( TrustManagerSelectChannelConnector.class );
    
    Properties props;
    
    protected ContextWrapper context = null;
    
    protected static final String defaultAlgorithm = "SunX509";
    
    
    public TrustManagerSocketConnector( Properties props ) {
        
        this.props = props;
        
    }
    
    protected void initializeSSLContext() throws IOException, GeneralSecurityException{
        
        log.debug( "TrustManagerSocketConnector.initializeSSLContext():" );
        
        String clientAuth = props.getProperty( "clientAuth" );

        if ( "true".equalsIgnoreCase( clientAuth )
                || "yes".equalsIgnoreCase( clientAuth ) )
            setNeedClientAuth( true );

        else if ( "want".equalsIgnoreCase( clientAuth ) )
            setWantClientAuth( true );
                
        context = new ContextWrapper(props);
        
    }
    @Override
    protected SSLServerSocketFactory createFactory() throws Exception {
    
        if (context == null)
            initializeSSLContext();
        
        return context.getServerSocketFactory();
    }

}
