package org.glite.authz.pap.server.standalone;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.PropertyConfigurator;
import org.glite.authz.pap.common.PAPConfiguration;
	
public class ShutdownClient {
	
	private static final String log4jLoggingProperties = "log4j.appender.MAIN=org.apache.log4j.ConsoleAppender\nlog4j.appender.MAIN.layout=org.apache.log4j.PatternLayout\nlog4j.rootLogger=OFF, MAIN";
	
	static void configureLogging(){
		
		Properties p = new Properties();
		try {
			p.load(new StringReader(log4jLoggingProperties));
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		PropertyConfigurator.configure(p);
	}
	
	static void printErrorMessageAndExit(Exception e){
		
		System.err.println("PAP shutdown error: "+e.getMessage());
		System.exit(1);
	}
	
	static void doShutdown(){
		
		PAPConfiguration conf = PAPConfiguration.instance();
		
		String shutdownEndpoint = String.format("http://localhost:%s/shutdown", 
				conf.getString(PAPConfiguration.STANDALONE_SERVICE_STANZA+".shutdown_port"));
		
		String shutdownCommand = conf.getString(PAPConfiguration.STANDALONE_SERVICE_STANZA+".shutdown_command");
		
		HttpClient httpClient = new HttpClient();
		
		GetMethod get = new GetMethod(shutdownEndpoint);
		
		if (shutdownCommand != null){
			get.setRequestHeader(new Header(ShutdownServlet.SHUTDOWN_COMMAND_HEADER_NAME, shutdownCommand));
		}
		
		try {
			
			httpClient.executeMethod(get);
		
		} catch (Exception e) {
			
			printErrorMessageAndExit(e);
		}
		
	}
	
	public static void main(String[] args) {
		
		configureLogging();
		
		if (System.getProperty("PAP_HOME") == null){
			
			System.err.println("Please set the PAP_HOME environment variable before running this command!");
			System.exit(1);
		}
		
		try{
			String papConfigurationHome = System.getProperty("PAP_HOME")+"/conf"; 
			PAPConfiguration.initialize(papConfigurationHome);
		
		}catch (Throwable t) {
			
			System.err.println("Error reading PAP configuration. Is the PAP_HOME system property/environment variable set correctly?");
			System.err.println(t.getMessage());
			t.printStackTrace(System.err);
			System.exit(1);
				
		}
		
		doShutdown();
	}
}
