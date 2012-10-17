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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.eclipse.jetty.util.log.Log;
import org.glite.authz.pap.common.PAPConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This servlet implements the PAP service shutdown logic.
 * 
 * @author andrea
 *
 */
public class ShutdownServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The logging facility **/ 
	public static final Logger logger = LoggerFactory.getLogger(ShutdownServlet.class);
	
	/** The name of the HTTP header coming in the shutdown request that contains the
	 * shutdown command.
	 */
	public static final String SHUTDOWN_COMMAND_HEADER_NAME = "PAP_SHUTDOWN_COMMAND";
	
	/**
	 * The thread that will trigger the shutdown.
	 */
	private Thread shutdownCommandThread;
	
	/**
	 * 
	 * @param shutdownThread, the thread that will shutdown the service upon request
	 */
	public ShutdownServlet(Thread shutdownThread) {
		this.shutdownCommandThread = shutdownThread;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String papShutdownCommand  = PAPConfiguration.instance().getString(PAPConfiguration.STANDALONE_SERVICE_STANZA+".shutdown_command");
		
		org.eclipse.jetty.util.log.Logger jettyLog = Log.getLogger(this.getClass().getName());
		jettyLog.info("Shutdown request received from {}.", req.getRemoteAddr());
		
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

