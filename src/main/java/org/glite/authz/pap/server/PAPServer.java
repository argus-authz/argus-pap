package org.glite.authz.pap.server;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.server.jetty.JettyShutdownCommand;
import org.glite.authz.pap.server.jetty.JettyShutdownService;
import org.glite.authz.pap.server.jetty.TrustManagerSocketConnector;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.concurrent.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PAPServer {

    final class PAPDefaults {

        static final int PORT = 8150;

        static final int SHUTDOWN_PORT = 8151;

        static final int MAX_REQUEST_QUEUE_SIZE = 0;

        static final int MAX_CONNECTIONS = 0;

    }

    private static final Logger log = LoggerFactory.getLogger( PAPServer.class );

    private static final String STANDALONE_STANZA = "standalone-configuration";

    private static final String TRUSTMANAGER_STANZA = STANDALONE_STANZA
            + ":trustmanager";

    private static final String CONF_DIR_OPTION_NAME = "conf-dir";

    private static final String REPO_DIR_OPTION_NAME = "repo-dir";

    private static final String PAP_DEFAULT_CONTEXT = "/glite-authz-pap";

    public static void main( String[] args ) {

        new PAPServer( args );

    }

    protected String papConfigurationDir;

    protected String papRepositoryDir;

    protected Server papServer;

    private WebAppContext webappContext;

    protected String getPAPWar() {

        return (String) System.getProperty( "GLITE_LOCATION" )
                + "/share/webapps/glite-authz-pap-standalone.war";
    }

    public PAPServer( String[] args ) {

        try {

            parseOptions( args );

            PAPConfiguration.initialize( papConfigurationDir, papRepositoryDir );

            configurePAPServer();

            papServer.start();

            if ( webappContext.getUnavailableException() != null )
                throw webappContext.getUnavailableException();

            papServer.join();

        } catch ( Throwable e ) {

            log
                    .error( "PAP encountered an error that could not be dealt with, shutting down!" );
            
            log.error( e.getMessage() );
            
            // Print stack trace if log is debug enabled!
            if (log.isDebugEnabled()) 
                log.error( e.getMessage(), e);
            

            try {
                papServer.stop();

            } catch ( Exception e1 ) {
                // Just ignore this
            }

            System.exit( 1 );
        }

    }

    private void configurePAPServer() {

        papServer = new Server( getInt( "port", PAPDefaults.PORT ) );

        int maxRequestQueueSize = getInt( "max_request_queue_size",
                PAPDefaults.MAX_REQUEST_QUEUE_SIZE );

        int maxConnections = getInt( "max_connections",
                PAPDefaults.MAX_CONNECTIONS );

        papServer.setSendServerVersion( false );
        papServer.setSendDateHeader( false );

        BlockingQueue <Runnable> requestQueue;

        if ( maxRequestQueueSize < 1 ) {
            requestQueue = new LinkedBlockingQueue <Runnable>();
        } else {
            requestQueue = new ArrayBlockingQueue <Runnable>(
                    maxRequestQueueSize );
        }

        ThreadPool threadPool = new ThreadPool( 5, maxConnections, 60,
                TimeUnit.SECONDS, requestQueue );

        papServer.setThreadPool( threadPool );

        TrustManagerSocketConnector connector = new TrustManagerSocketConnector(
                getTrustmanagerConfiguration() );

        connector.setPort( getInt( "port", PAPDefaults.PORT ) );

        papServer.setConnectors( new Connector[] { connector } );

        // Create shutdown service
        JettyShutdownCommand papShutdownCommand = new JettyShutdownCommand(
                papServer );

        // Create a Shutdown Service
        JettyShutdownService.startJettyShutdownService( 8151, Collections
                .singletonList( (Runnable) papShutdownCommand ) );
        
        
        // Create a webapp context for the PAP web application
        webappContext = new WebAppContext();

        webappContext.setContextPath( PAP_DEFAULT_CONTEXT );
        webappContext.setWar( getPAPWar() );

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers( new Handler[] { webappContext,
                new DefaultHandler() } );

        papServer.setHandler( handlers );
        
    }

    
    private int getInt( String key, int defaultValue ) {

        PAPConfiguration conf = PAPConfiguration.instance();
        return conf.getInt( STANDALONE_STANZA + "." + key, defaultValue );
    }

    private Properties getTrustmanagerConfiguration() {

        PAPConfiguration papConf = PAPConfiguration.instance();

        Properties tmProps = new Properties();

        Configuration tmConf = papConf.subset( TRUSTMANAGER_STANZA );
        Iterator <String> tmConfKeys = tmConf.getKeys();

        while ( tmConfKeys.hasNext() ) {
            String key = tmConfKeys.next();
            tmProps.put( key, tmConf.getProperty( key ) );
        }

        return tmProps;

    }

    protected void parseOptions( String[] args ) {

        if ( args.length > 0 ) {

            int currentArg = 0;

            while ( currentArg < args.length ) {

                if ( !args[currentArg].startsWith( "--" ) ) {
                    usage();
                } else if ( args[currentArg].equalsIgnoreCase( "--"
                        + CONF_DIR_OPTION_NAME ) ) {
                    papConfigurationDir = args[currentArg + 1];
                    log.info( "Starting PAP with configuration dir: {}",
                            papConfigurationDir );
                    currentArg = currentArg + 2;
                } else if ( args[currentArg].equalsIgnoreCase( "--"
                        + REPO_DIR_OPTION_NAME ) ) {
                    papRepositoryDir = args[currentArg + 1];
                    log.info( "Starting PAP with repo dir: {}",
                            papRepositoryDir );
                    currentArg = currentArg + 2;
                } else
                    usage();

            }

        }
    }

    private void usage() {

        String usage = "PAPServer [--" + CONF_DIR_OPTION_NAME
                + " <confDir>] [--" + REPO_DIR_OPTION_NAME + " <repoDir>]";

        System.out.println( usage );
        System.exit( 0 );
    }

}
