/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.server.standalone;

import java.security.Security;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.security.trustmanager.ContextWrapper;
import org.glite.security.util.CaseInsensitiveProperties;
import org.italiangrid.utils.https.JettyShutdownTask;
import org.italiangrid.utils.https.SSLOptions;
import org.italiangrid.utils.https.ServerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The standalone PAP daemon
 */
public final class PAPServer {
	
	/**
	 * 
	 * Useful defaults for the standalone service
	 * 
	 */
	final class PAPStandaloneServiceDefaults {

		/**
		 * The service hostname
		 */
		static final String HOSTNAME = "localhost";

		/**
		 * The service port on which the pap will listen to request
		 */
		static final int PORT = 8150;

		/**
		 * The shutdown service port
		 */
		static final int SHUTDOWN_PORT = 8151;

		/**
		 * Max request queue size. -1 means unbounded queue is used.
		 */
		static final int MAX_REQUEST_QUEUE_SIZE = -1;

		/**
		 * Max concurrent connections.
		 */
		static final int MAX_CONNECTIONS = 64;

		/**
		 * Default certificate path for the service
		 */
		static final String CERTIFICATE_PATH = "/etc/grid-security/hostcert.pem";

		/**
		 * Default private key path for the service
		 */
		static final String PRIVATE_KEY_PATH = "/etc/grid-security/hostkey.pem";

		/**
		 * Default path for CA certificates
		 */
		static final String CA_PATH = "/etc/grid-security/certificates";

		/**
		 * Should CRLs be checked by trustmanager?
		 */
		static final boolean CRL_ENABLED = true;
		
		/**
		 * How frequently the PAP should update CRLs, CAs and namespaces from the filesystem.
		 * The interval is defined as a string with the following format:
		 * <code>N{s,m,h,d}</code> 
		 * 
		 * where N in the number of either (s=seconds, m=minutes, h=hours, d=days).
		 * 
		 * Example: 30m 
		 * 
		 * which means 30 minutes.
		 *  
		 * The default is 30 minutes.
		 */
		static final String CRL_UPDATE_INTERVAL = "30m";
		
		
		/**
		 * The directory containing all the CA certificates, CRLs and namespace definitions.
		 */
		static final String TRUST_STORE_DIR = "/etc/grid-security/certificates";
		

	}

	private static final String DEFAULT_WAR_LOCATION = System
			.getProperty("PAP_HOME")
			+ "/wars/pap.war";

	private static final Logger log = LoggerFactory.getLogger(PAPServer.class);

	/**
	 * The option name used by callers to specify where the pap server should
	 * look for the configuration
	 */
	private static final String CONF_DIR_OPTION_NAME = "conf-dir";

	/**
	 * The pap configuration directory
	 */
	protected String papConfigurationDir;

	/**
	 * The pap jetty http server
	 */
	protected Server papServer;

	/**
	 * The jetty webapp context in which the pap wep application is configured
	 */
	private WebAppContext webappContext;

	/**
	 * Constructor. Parses the configuration and starts the server.
	 * 
	 * @param args
	 *            . the command line arguments as passed by the
	 *            {@link #main(String[])} method
	 */
	public PAPServer(String[] args) {

		try {

			parseOptions(args);
			
			Security.addProvider(new BouncyCastleProvider());

			PAPConfiguration.initialize(papConfigurationDir);

			configurePAPServer();

			papServer.start();

			if (webappContext.getUnavailableException() != null)
				throw webappContext.getUnavailableException();

			papServer.join();

		} catch (Throwable e) {

			log
					.error("PAP encountered an error that could not be dealt with, shutting down!");

			log.error(e.getMessage());

			// Also print error message to standard error

			System.err
					.println("PAP encountered an error that could not be dealt with, shutting down!");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);

			if (log.isDebugEnabled())
				log.error(e.getMessage(), e);

			try {
				papServer.stop();

			} catch (Exception e1) {
				// Just ignore this
			}

			System.exit(1);
		}

	}
	
	
	private SSLOptions getSSLOptions(){
		SSLOptions options = new SSLOptions();
		
		options.setCertificateFile(getStringFromSecurityConfiguration(
				"certificate", PAPStandaloneServiceDefaults.CERTIFICATE_PATH));
		options.setKeyFile(getStringFromSecurityConfiguration(
				"private_key", PAPStandaloneServiceDefaults.PRIVATE_KEY_PATH));
		
		options.setNeedClientAuth(true);
		options.setWantClientAuth(true);
		options.setTrustStoreDirectory(getStringFromSecurityConfiguration("trust_store_dir", String
				.valueOf(PAPStandaloneServiceDefaults.TRUST_STORE_DIR)));
		
		Long trustStoreUpdateInterval = Long.parseLong(getStringFromSecurityConfiguration("crl_update_interval", PAPStandaloneServiceDefaults.CRL_UPDATE_INTERVAL));
		
		options.setTrustStoreRefreshIntervalInMsec(trustStoreUpdateInterval);
		
		return options;
	}

//	private Connector configureConnector(String host, int port){
//		
//		
//		CaseInsensitiveProperties props = new CaseInsensitiveProperties(buildTrustmanagerConfiguration());
//		
//		try {
//			
//			ContextWrapper context = new ContextWrapper(props, false);
//			
//			JettySslSelectChannelConnector connector = new JettySslSelectChannelConnector(context.getKeyManager(),context.m_trustmanager);
//			
//			connector.setPort(port);
//			connector.setHost(host);
//		
//			connector.setWantClientAuth(true);
//			connector.setNeedClientAuth(true);
//			
//			log.info("PAP service will listen on {}:{}",
//					new Object[] { host, port });
//			
//			return connector;
//			
//		
//		} catch (Exception e) {
//			
//			log.error("Error initializing trustmanager connector: "+e.getMessage());
//			if (log.isDebugEnabled())
//				log.error("Error initializing trustmanager connector: "+e.getMessage(),e);
//			
//			throw new PAPException(e);
//		}
//		
//		
//	}
	
	
	private void configureRequestQueue(){
		
		int maxRequestQueueSize = getIntFromStandaloneConfiguration(
				"max_request_queue_size",
				PAPStandaloneServiceDefaults.MAX_REQUEST_QUEUE_SIZE);

		log.debug("maxRequestQueueSize = {}", maxRequestQueueSize);

		int maxConnections = getIntFromStandaloneConfiguration(
				"max_connections", PAPStandaloneServiceDefaults.MAX_CONNECTIONS);

		if (maxConnections <= 0) {
			log
					.error("Please specify a positive value for the 'maxConnections' configuration parameter!");
			log.error("Will use the hardcoded default '{}' instead...",
					PAPStandaloneServiceDefaults.MAX_CONNECTIONS);
			maxConnections = PAPStandaloneServiceDefaults.MAX_CONNECTIONS;
		}

		log.info("maxConnections = {}", maxConnections);
		
		BlockingQueue<Runnable> requestQueue = new ArrayBlockingQueue<Runnable>(maxRequestQueueSize);
		
		ThreadPool threadPool = new ExecutorThreadPool(5,maxConnections, 60, TimeUnit.SECONDS, requestQueue);
		papServer.setThreadPool(threadPool);
		
	}
	
	/**
	 * Performs the jetty server configuration
	 */
	private void configurePAPServer() {

		log.info("Configuring jetty PAP server...");
		
		int port = getIntFromStandaloneConfiguration("port",
			PAPStandaloneServiceDefaults.PORT);
	
		String host = getStringFromStandaloneConfiguration("hostname",
			PAPStandaloneServiceDefaults.HOSTNAME);
		
		
		SSLOptions options = getSSLOptions();
				
		papServer = ServerFactory.newServer(host, port, options);
		configureRequestQueue();    
		

		JettyShutdownTask papShutdownCommand = new JettyShutdownTask(
				papServer);

		PapShutdownAndStatusService.startPAPShutdownAndStatusService(8151, Collections
				.singletonList((Runnable) papShutdownCommand));

		webappContext = new WebAppContext();

		webappContext.setContextPath("/"
				+ PAPConfiguration.DEFAULT_WEBAPP_CONTEXT);
		webappContext.setWar(DEFAULT_WAR_LOCATION);
		webappContext.setParentLoaderPriority(true);

		HandlerCollection handlers = new HandlerCollection();
		
		handlers.setHandlers(new Handler[] { webappContext,
				new DefaultHandler() });

		papServer.setHandler(handlers);

	}

	/**
	 * Utility method to fetch an int configuration parameter out of the
	 * standalone-service configuration
	 * 
	 * @param key
	 *            , the configuration parameter key
	 * @param defaultValue
	 *            , a default value in case the parameter is not defined
	 * @return the configuration parameter value
	 */
	private int getIntFromStandaloneConfiguration(String key, int defaultValue) {

		PAPConfiguration conf = PAPConfiguration.instance();
		return conf.getInt(PAPConfiguration.STANDALONE_SERVICE_STANZA + "."
				+ key, defaultValue);
	}

	/**
	 * Utility method to fetch a string configuration parameter out of the
	 * security configuration
	 * 
	 * @param key
	 *            , the configuration parameter key
	 * @param defaultValue
	 *            , a default value in case the parameter is not defined
	 * @return the configuration parameter value
	 * 
	 */
	private String getStringFromSecurityConfiguration(String key,
			String defaultValue) {

		PAPConfiguration conf = PAPConfiguration.instance();
		return conf.getString(PAPConfiguration.SECURITY_STANZA + "." + key,
				defaultValue);
	}

	/**
	 * Utility method to fetch a string configuration parameter out of the
	 * security configuration
	 * 
	 * @param key
	 *            , the configuration parameter key
	 * @param defaultValue
	 *            , a default value in case the parameter is not defined
	 * @return the configuration parameter value
	 */
	private String getStringFromStandaloneConfiguration(String key,
			String defaultValue) {

		PAPConfiguration conf = PAPConfiguration.instance();
		return conf.getString(PAPConfiguration.STANDALONE_SERVICE_STANZA + "."
				+ key, defaultValue);

	}

	/**
	 * Parses command line options
	 * 
	 * @param args
	 *            , the command line options
	 */
	protected void parseOptions(String[] args) {

		if (args.length > 0) {

			int currentArg = 0;

			while (currentArg < args.length) {

				if (!args[currentArg].startsWith("--")) {
					usage();
				} else if (args[currentArg].equalsIgnoreCase("--"
						+ CONF_DIR_OPTION_NAME)) {
					papConfigurationDir = args[currentArg + 1];
					log.info("Starting PAP with configuration dir: {}",
							papConfigurationDir);
					currentArg = currentArg + 2;

				} else
					usage();

			}

		}
	}

	/**
	 * Prints a usage message and exits with status 1
	 */
	private void usage() {

		String usage = "PAPServer [--" + CONF_DIR_OPTION_NAME + " <confDir>]";

		System.out.println(usage);
		System.exit(1);
	}

	/**
	 * Runs the service
	 * 
	 * @param args
	 *            , the command line arguments
	 */
	public static void main(String[] args) {

		new PAPServer(args);

	}

}
