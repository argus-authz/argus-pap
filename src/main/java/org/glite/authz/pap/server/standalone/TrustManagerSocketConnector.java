package org.glite.authz.pap.server.standalone;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.net.ssl.SSLServerSocketFactory;

import org.glite.security.trustmanager.ContextWrapper;
import org.mortbay.jetty.security.SslSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A jetty socket channel connector that leverages glite-security-trustmanager
 * to create SSLServerSocket factories.
 * 
 *
 */
public class TrustManagerSocketConnector extends SslSocketConnector {
    
    private static final Logger log = LoggerFactory
    .getLogger( TrustManagerSelectChannelConnector.class );
  
    /**
     * The properties used to configure trustmanager
     */
    Properties props;
    
    /**
     * The trustmanager context
     */
    protected ContextWrapper context = null;
      
    
    /**
     * Constructor
     * 
     * @param props, the properties used to configure trustmanager
     */
    public TrustManagerSocketConnector( Properties props ) {
        
        this.props = props;
        
    }
    
    /**
     * Initializes and configures the trustmanager SSL context.
     *  
     * @throws IOException
     * @throws GeneralSecurityException
     */
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
