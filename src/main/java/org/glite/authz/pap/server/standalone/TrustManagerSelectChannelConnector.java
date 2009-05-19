package org.glite.authz.pap.server.standalone;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.glite.security.trustmanager.ContextWrapper;
import org.mortbay.io.nio.SelectChannelEndPoint;
import org.mortbay.io.nio.SelectorManager.SelectSet;
import org.mortbay.jetty.security.SslHttpChannelEndPoint;
import org.mortbay.jetty.security.SslSelectChannelConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A jetty select channel connector that leverages glite-security-trustmanager
 * to create the SSL context.
 *
 */
public class TrustManagerSelectChannelConnector extends
        SslSelectChannelConnector {

	static{
		
		Security.addProvider(new BouncyCastleProvider());
		
	}
    private static final Logger log = LoggerFactory
            .getLogger( TrustManagerSelectChannelConnector.class );

    /** 
     * The properties used to configure the trustmanager ssl context
     */
    Properties props;

    /**
     * The SSL context
     */
    protected SSLContext context = null;


    /**
     * Constructor
     * 
     * 
     * @param props, the properties used to configure trustmanager
     * 
     */
    public TrustManagerSelectChannelConnector( Properties props ) {

        this.props = props;

    }

    /**
     * Initializes the SSL context via glite-security trustmanager
     * 
     * @throws Exception, if something goes wrong
     */
    protected void initializeSSLContext() throws Exception {

        log
                .debug( "TrustManagerSelectChannelConnector.initializeSSLContext():" );

        // Override clientAuth settings for trustmanager
        // The PAP authz system always need clientAuth on!
        props.setProperty( "clientAuth", "yes" );

        setNeedClientAuth( true );
        setWantClientAuth( true );

        
        ContextWrapper tmContext = new ContextWrapper( props );
        context = tmContext.getContext();
    }

    @Override
    protected SSLContext createSSLContext() throws Exception {

        if ( context == null )
            initializeSSLContext();

        return context;
    }

    @Override
    protected SSLEngine createSSLEngine() throws IOException {

        SSLEngine engine = context.createSSLEngine();

        if ( getWantClientAuth() )
            engine.setWantClientAuth( getWantClientAuth() );
        else {
            engine.setNeedClientAuth( getNeedClientAuth() );
        }

        if ( getExcludeCipherSuites() != null
                && getExcludeCipherSuites().length > 0 ) {
            List <String> excludedCSList = Arrays
                    .asList( getExcludeCipherSuites() );
            String[] enabledCipherSuites = engine.getEnabledCipherSuites();
            List <String> enabledCSList = new ArrayList <String>( Arrays
                    .asList( enabledCipherSuites ) );

            for ( String cipherName : excludedCSList ) {
                if ( enabledCSList.contains( cipherName ) ) {
                    enabledCSList.remove( cipherName );
                }
            }
            enabledCipherSuites = enabledCSList
                    .toArray( new String[enabledCSList.size()] );

            engine.setEnabledCipherSuites( enabledCipherSuites );
        }

        return engine;

    }

    @Override
    protected SelectChannelEndPoint newEndPoint( SocketChannel channel,
            SelectSet selectSet, SelectionKey key ) throws IOException {

        SSLEngine engine = createSSLEngine();
        engine.setUseClientMode( false );

        return new SslHttpChannelEndPoint( this, channel, selectSet, key,
                engine );

    }

}
