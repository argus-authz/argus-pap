package org.glite.authz.pap.server;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.axis.transport.http.AxisServlet;
import org.apache.commons.configuration.Configuration;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.PAPContextListener;
import org.glite.authz.pap.server.jetty.TrustManagerSocketConnector;
import org.glite.authz.pap.servlet.SecurityContextFilter;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.concurrent.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PAPServer {
    
    private static final Logger log = LoggerFactory.getLogger( PAPServer.class );
    
    private static final String STANDALONE_STANZA = "standalone-configuration";
    
    private static final String TRUSTMANAGER_STANZA = STANDALONE_STANZA+":trustmanager"; 
    
    
    private static void configureConnector(Connector connector){
        
        connector.setHost( getString( "host", "localhost" ));
        connector.setPort( getInt( "port", 4554));

        // Add more sophisticated configuration of the connector below
        
    }
    
    public static void main( String[] args ) {

        // Initialize PAP configuration
        PAPConfiguration.initialize();
        
        int maxRequestQueueSize  = getInt("max_request_queue_size", 0);
        int maxConnections = getInt("max_connections", 30 );
        
        Server httpServer = new Server(getInt( "port", 4554));
        
        // Are these needed for the PAP?
        httpServer.setSendServerVersion(false);
        httpServer.setSendDateHeader(false);
        
        BlockingQueue<Runnable> requestQueue;
        
        if (maxRequestQueueSize < 1) {
            requestQueue = new LinkedBlockingQueue<Runnable>();
        } else {
            requestQueue = new ArrayBlockingQueue<Runnable>(maxRequestQueueSize);
        }
        
        ThreadPool threadPool = new ThreadPool(5, maxConnections, 60, TimeUnit.SECONDS, requestQueue);
        httpServer.setThreadPool(threadPool);
        
        TrustManagerSocketConnector connector = new TrustManagerSocketConnector(getTrustmanagerConfiguration());
        
        // TrustManagerSelectChannelConnector connector = new TrustManagerSelectChannelConnector(getTrustmanagerConfiguration());
        // SslSelectChannelConnector connector = new SslSelectChannelConnector();
        
        configureConnector( connector );
        
        httpServer.setConnectors( new Connector[]{connector} );
        
        Context servletContext = new Context(httpServer,"/", false, false);
        
        FilterHolder securityFilter = new FilterHolder(new SecurityContextFilter());
        securityFilter.setName( "Security context filter" );
        servletContext.addFilter( securityFilter, "/*", 0);
        
        servletContext.addEventListener( new PAPContextListener() );
        
        ServletHolder axisServlet = new ServletHolder(new AxisServlet());
        axisServlet.setName( "Axis servlet" );
        servletContext.addServlet( axisServlet, "/services/*");
        
        ServletHolder testServlet = new ServletHolder(new TestServlet());
        testServlet.setName( "Test servlet" );
        servletContext.addServlet( testServlet, "/test" );
        
        try {
            httpServer.start();
            log.info("PAP started and listening on {}:{}", connector.getHost(), connector.getPort());
            httpServer.join();
            
        } catch (Exception e) {
            log.info("PAP encountered an error that could not be dealt with, shutting down.", e);
            
            try {
                httpServer.stop();
            
            } catch ( Exception e1 ) {
                // Just ignore this
            }
        }
        
    }
    
    
    private static String getString(String key, String defaultValue){
        
        PAPConfiguration conf = PAPConfiguration.instance();
        return conf.getString( STANDALONE_STANZA+"."+key, defaultValue ); 
        
    }
    
    
    private static int getInt(String key, int defaultValue){
        
        PAPConfiguration conf = PAPConfiguration.instance();
        return conf.getInt( STANDALONE_STANZA+"."+key, defaultValue ); 
    }
    
    
    private static Properties getTrustmanagerConfiguration(){
        
                      
        PAPConfiguration papConf = PAPConfiguration.instance();
        
        Properties tmProps = new Properties();
        
        Configuration tmConf = papConf.subset( TRUSTMANAGER_STANZA );
        Iterator<String> tmConfKeys = tmConf.getKeys();
        
        while(tmConfKeys.hasNext()){
            String key = tmConfKeys.next();
            tmProps.put( key, tmConf.getProperty( key ) );
        }
            
        return tmProps;
        
    }
}
