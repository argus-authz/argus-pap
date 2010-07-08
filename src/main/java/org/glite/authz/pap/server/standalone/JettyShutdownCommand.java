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

import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A command that shuts down a Jetty {@link Server} if it's currently running. */
public class JettyShutdownCommand implements Runnable {

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(JettyShutdownCommand.class);

    /** Server to be shutdown. */
    private Server httpServer;

    /**
     * Constructor.
     * 
     * @param targetServer server to be shutdown
     */
    public JettyShutdownCommand(Server targetServer) {
        httpServer = targetServer;
    }

    /** {@inheritDoc} */
    public void run() {
        if (httpServer.isRunning()) {
            try {
                httpServer.stop();
            } catch (Exception e) {
                log.error("Unable to shutdown HTTP server", e);
                System.exit(1);
            }
        }
    }
}