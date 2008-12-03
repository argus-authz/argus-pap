package org.glite.authz.pap.server.jetty;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.glite.security.trustmanager.ContextWrapper;
import org.mortbay.io.nio.SelectChannelEndPoint;
import org.mortbay.io.nio.SelectorManager.SelectSet;
import org.mortbay.jetty.security.SslHttpChannelEndPoint;
import org.mortbay.jetty.security.SslSelectChannelConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustManagerSelectChannelConnector extends SslSelectChannelConnector {

        
    private static final Logger log = LoggerFactory
            .getLogger( TrustManagerSelectChannelConnector.class );

    Properties props;

    protected SSLContext context = null;
    
    protected static final String defaultAlgorithm = "SunX509";

    public TrustManagerSelectChannelConnector( Properties props ) {

        this.props = props;

    }

    protected void initializeSSLContext() throws Exception{
        
        String clientAuth = props.getProperty( "clientAuth" );

        if ( "true".equalsIgnoreCase( clientAuth )
                || "yes".equalsIgnoreCase( clientAuth ) )
            setNeedClientAuth( true );

        else if ( "want".equalsIgnoreCase( clientAuth ) )
            setWantClientAuth( true );
                
        ContextWrapper tmContext = new ContextWrapper(props);
        context = tmContext.getContext();
    }

    @Override
    protected SSLContext createSSLContext() throws Exception {
        
        if (context == null)
            initializeSSLContext();
        
        return context;
    }
    
    
    @Override
    protected SSLEngine createSSLEngine() throws IOException {
    
        SSLEngine engine = context.createSSLEngine(); 
         
        if (getWantClientAuth())
            engine.setWantClientAuth(getWantClientAuth());
        else{
            engine.setNeedClientAuth( getNeedClientAuth() );
        }
                
        
        if (getExcludeCipherSuites()!= null && getExcludeCipherSuites().length > 0)
        {
            List<String> excludedCSList=Arrays.asList(getExcludeCipherSuites());
            String[] enabledCipherSuites=engine.getEnabledCipherSuites();
            List<String> enabledCSList=new ArrayList<String>(Arrays.asList(enabledCipherSuites));

            for (String cipherName : excludedCSList)
            {
                if (enabledCSList.contains(cipherName))
                {
                    enabledCSList.remove(cipherName);
                }
            }
            enabledCipherSuites=enabledCSList.toArray(new String[enabledCSList.size()]);

            engine.setEnabledCipherSuites(enabledCipherSuites);
        }
    	
        return engine;     	    	
    	 
    }
    
    @Override
    protected SelectChannelEndPoint newEndPoint( SocketChannel channel,
            SelectSet selectSet, SelectionKey key ) throws IOException {
    
        SSLEngine engine = createSSLEngine();
        engine.setUseClientMode( false );
        
        return new SslHttpChannelEndPoint(this,channel,selectSet,key,engine);
        
    }
    
}
