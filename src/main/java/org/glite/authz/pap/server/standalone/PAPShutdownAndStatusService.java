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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glite.authz.pap.monitoring.MonitoringServlet;
import org.italiangrid.utils.https.JettyRunThread;

/**
 * A Jetty instance that listens on a given port for a request to the URL path
 * <em>/shutdown</em> and <em>/status</em>.
 * 
 * This command starts a separate Jetty instance that binds to 127.0.0.1 on a
 * port given during service construction. When a GET request is received a
 * thread is spawned that runs each given shutdown command in turn. Finally,
 * after all commands have been run, the created shutdown service is stopped as
 * well. The shutdown process occurs asynchronously and does NOT block the
 * return of the GET request.
 * 
 * Additionally, the same shutdown procedure is bound as a JVM shutdown hook in
 * the event that the process is terminated in that fashion.
 */
public class PAPShutdownAndStatusService {

  /**
   * Creates and starts the PAP shutdown and status service.
   * 
   * @param shutdownPort
   *          port on which the service will listen
   * @param shutdownCommands
   *          list of commands to run at shutdown time
   */
  public static void startPAPShutdownAndStatusService(int shutdownPort,
    List<Runnable> shutdownCommands) {

    final Server shutdownService = new Server();

    final Thread shutdownCommandThread = buildServiceShutdownThread(
      shutdownService, shutdownCommands);

    ServerConnector connector = new ServerConnector(shutdownService);
    connector.setHost("localhost");
    connector.setPort(shutdownPort);
    shutdownService.addConnector(connector);

    ServletContextHandler servletContext = new ServletContextHandler(
      ServletContextHandler.NO_SESSIONS);
    servletContext.setDisplayName("Shutdown Controller");

    ServletHolder shutdownServlet = new ServletHolder(new ShutdownServlet(
      shutdownCommandThread));
    servletContext.addServlet(shutdownServlet, "/shutdown");

    ServletHolder statusServlet = new ServletHolder(new MonitoringServlet());
    servletContext.addServlet(statusServlet, "/status");

    JettyRunThread shutdownServiceRunThread = new JettyRunThread(
      shutdownService);
    shutdownServiceRunThread.start();

    shutdownService.setHandler(servletContext);
  }

  /**
   * Creates the thread that run in order to shutdown everything. This will
   * create a new shutdown command, added to the end of the given list, that
   * will shutdown the shutdown service currently being created.
   * 
   * @param shutdownService
   *          the shutdown service being created
   * @param commands
   *          the shutdown commands to run before stopping the shutdown service
   * 
   * @return the shutdown thread
   */
  private static Thread buildServiceShutdownThread(
    final Server shutdownService, List<Runnable> commands) {

    final Runnable shutdownShutdownServiceCommand = new PAPShutdownCommand(
      shutdownService);

    final List<Runnable> shutdownCommands;
    if (commands == null || commands.isEmpty()) {
      shutdownCommands = Collections
        .singletonList(shutdownShutdownServiceCommand);
    } else {
      shutdownCommands = new ArrayList<Runnable>(commands);
      shutdownCommands.add(shutdownShutdownServiceCommand);
    }

    final Thread shutdownCommandThread = new Thread() {

      public void run() {

        for (Runnable shutdownCommand : shutdownCommands) {
          shutdownCommand.run();
        }
      }
    };

    Runtime.getRuntime().addShutdownHook(new Thread(shutdownCommandThread));
    return shutdownCommandThread;
  }
}