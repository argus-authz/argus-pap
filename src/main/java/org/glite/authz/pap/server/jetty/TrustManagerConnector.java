package org.glite.authz.pap.server.jetty;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.glite.security.trustmanager.ContextWrapper;
import org.mortbay.io.Connection;
import org.mortbay.io.nio.SelectChannelEndPoint;
import org.mortbay.io.nio.SelectorManager.SelectSet;
import org.mortbay.jetty.security.SslSelectChannelConnector;
import org.mortbay.log.Log;
import org.mortbay.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustManagerConnector extends SslSelectChannelConnector {

    static{
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    private static final Logger log = LoggerFactory
            .getLogger( TrustManagerConnector.class );

    Properties props;

    protected SSLContext context = null;
    
    protected static final String defaultAlgorithm = "SunX509";

    public TrustManagerConnector( Properties props ) {

        this.props = props;

    }

    protected void init() throws Exception{

        log.debug( "TrustManagerConnector.init():" );

        String clientAuth = props.getProperty( "clientAuth" );

        if ( "true".equalsIgnoreCase( clientAuth )
                || "yes".equalsIgnoreCase( clientAuth ) )
            setNeedClientAuth( true );

        else if ( "want".equalsIgnoreCase( clientAuth ) )
            setWantClientAuth( true );

        String sslProtocol = props.getProperty( "sslProtocol" );

        if ( sslProtocol == null )
            sslProtocol = ContextWrapper.SSL_PROTOCOL_DEFAULT;
        
        setProtocol( sslProtocol );
        
        // Certificate encoding algorithm (e.g., SunX509)
        String algorithm = (String) props.getProperty( "algorithm" );

        if ( algorithm == null ) {
            algorithm = defaultAlgorithm;
        }

        setAlgorithm( algorithm );
        
        String keystoreType = (String) props.getProperty( "keystoreType" );

        if ( keystoreType == null ) {
            keystoreType = ContextWrapper.KEYSTORE_TYPE_DEFAULT;
        }

        setKeystoreType( keystoreType );
        
        // no excluded cipher suites!
        setExcludeCipherSuites( null );
        
        setProvider( "BC" );
        
        ContextWrapper tmContext = new ContextWrapper(props);
        context = tmContext.getContext();
        
              

    }

    @Override
    protected SSLContext createSSLContext() throws Exception {
        
        if (context == null)
            init();
        
        return context;
    }
    
    @Override
    protected SelectChannelEndPoint newEndPoint( SocketChannel channel,
            SelectSet selectSet, SelectionKey key ) throws IOException {
    
        // TODO Auto-generated method stub
        return super.newEndPoint( channel, selectSet, key );
    }
    
    @Override
    protected Connection newConnection( SocketChannel channel,
            SelectChannelEndPoint endpoint ) {
    
        // TODO Auto-generated method stub
        return super.newConnection( channel, endpoint );
    }
}
