package org.glite.authz.pap.server.standalone;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.glite.authz.pap.common.PAPConfiguration;

public class ShutdownClient {

	static void printErrorMessageAndExit(Exception e) {

		System.err.println("PAP shutdown error: " + e.getMessage());
		System.exit(1);
	}

	static void doShutdown() {

		PAPConfiguration conf = PAPConfiguration.instance();

		String shutdownEndpoint = String.format("http://localhost:%s/shutdown",
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

	public static void main(String[] args) {

		// Switch off log4j logging
		LogManager.getRootLogger().setLevel(Level.OFF);

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
