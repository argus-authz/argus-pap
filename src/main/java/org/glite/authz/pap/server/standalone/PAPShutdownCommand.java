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

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPShutdownCommand implements Runnable {
	Logger log = LoggerFactory.getLogger(PAPShutdownCommand.class);
	
	final Server server;
	
	public PAPShutdownCommand(Server s) {
		this.server = s;
	}
	
	
	public void run() {
		
		log.debug("Shutting down server: "+server);
		if (server.isRunning()){
			try{
				server.stop();
				if (server.isStopped())
					log.debug("Server {} stopped.", server);
			
			}catch (Throwable e) {
				log.error("Unable to shutdown http server", e);
				System.exit(1);
			}
		}

	}

}
