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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import org.glite.authz.pap.common.PAPConfiguration;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * This class implement the client that request the shutdown of the local PAP
 * service.
 * 
 * @author andrea
 * 
 */
public class ShutdownClient {

    /**
     * Prints the message of the exception passed as argument and exits
     * 
     * @param e
     *            , the exception to be printed
     */
    static void printErrorMessageAndExit(Exception e) {

	System.err.println("PAP shutdown error: " + e.getMessage());
	System.exit(1);
    }

    /**
     * Triggers the shutdown of the local PAP service.
     */
    static void doShutdown() {

	PAPConfiguration conf = PAPConfiguration.instance();

	String shutdownEndpoint = String.format(
		"http://localhost:%s/shutdown",
		conf.getString(PAPConfiguration.STANDALONE_SERVICE_STANZA
			+ ".shutdown_port"));

	String shutdownCommand = conf
		.getString(PAPConfiguration.STANDALONE_SERVICE_STANZA
			+ ".shutdown_command");

	HttpClient httpClient = new HttpClient();

	GetMethod get = new GetMethod(shutdownEndpoint);

	if (shutdownCommand != null) {
	    get.setRequestHeader(new Header(
		    ShutdownServlet.SHUTDOWN_COMMAND_HEADER_NAME,
		    shutdownCommand));
	}

	try {

	    httpClient.executeMethod(get);

	} catch (Exception e) {

	    printErrorMessageAndExit(e);
	}

    }

    /** Disables logging messages (logback version) */
    private static void disableLogBackLibraryLogging() {
    	LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger rootLogger = lc.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		rootLogger.setLevel(Level.OFF);
    }

    
    private static void disableLibraryLogging(){
	
	// Switch off log4j logging
	// LogManager.getRootLogger().setLevel(Level.OFF);
	
    }
    
    public static void main(String[] args) {

	disableLibraryLogging();
    // disableLogBackLibraryLogging();

	if (System.getProperty("PAP_HOME") == null) {

	    System.err
		    .println("Please set the PAP_HOME environment variable before running this command!");
	    System.exit(1);
	}

	try {
	    String papConfigurationHome = System.getProperty("PAP_HOME")
		    + "/conf";
	    PAPConfiguration.initialize(papConfigurationHome);

	} catch (Throwable t) {

	    System.err
		    .println("Error reading PAP configuration. Is the PAP_HOME system property/environment variable set correctly?");
	    System.err.println(t.getMessage());
	    t.printStackTrace(System.err);
	    System.exit(1);

	}

	doShutdown();
    }
}
