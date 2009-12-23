package org.glite.authz.pap.server.standalone;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.glite.authz.pap.common.PAPConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Logger logger = LoggerFactory.getLogger(ShutdownServlet.class);
	
	public static final String SHUTDOWN_COMMAND_HEADER_NAME = "PAP_SHUTDOWN_COMMAND";
	
	private Thread shutdownCommandThread;
	
	public ShutdownServlet(Thread shutdownThread) {
		this.shutdownCommandThread = shutdownThread;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String papShutdownCommand  = PAPConfiguration.instance().getString(PAPConfiguration.STANDALONE_SERVICE_STANZA+".shutdown_command");
		
		if (papShutdownCommand == null){
			
			shutdownCommandThread.start();
		
		}else {
			
			String shutdownCommand = req.getHeader(SHUTDOWN_COMMAND_HEADER_NAME);
			
			if (shutdownCommand != null && shutdownCommand.equals(papShutdownCommand))
				shutdownCommandThread.start();
			else
				logger.warn("Shutdown attempted with invalid command string!");	
			
		}
	}
}

