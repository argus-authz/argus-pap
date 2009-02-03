package org.glite.authz.pap.server.jetty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

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
        
        // Override clientAuth settings for trustmanager
        // The PAP authz system always need clientAuth on!
        props.setProperty( "clientAuth", "yes" );
        
        setNeedClientAuth( true );
        setWantClientAuth( true );
                
        // context = new ContextWrapper(props,false);
        context = new ContextWrapper(props);
    }
    @Override
    protected SSLServerSocketFactory createFactory() throws Exception {
    
        if (context == null)
            initializeSSLContext();
        
        return context.getServerSocketFactory();
    }
    

}
