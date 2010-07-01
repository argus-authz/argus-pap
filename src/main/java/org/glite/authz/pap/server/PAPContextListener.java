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

/**************************************************************************

 Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 File : PAPContextListener.java

 Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A servlet context listener used to get informed about PAP web application lifecycle
 * events. 
 * 
 * @author Valerio Venturi (valerio.venturi@cnaf.infn.it)
 * 
 */
public class PAPContextListener implements ServletContextListener {

    final Logger logger = LoggerFactory.getLogger(PAPContextListener.class);

    /**
     * Constructor
     */
    public PAPContextListener() {

	super();
    }

    /**
     * {@inheritDoc}
     */
    public void contextInitialized(ServletContextEvent contextEvent) {

	// get the servlet context
	ServletContext servletContext = contextEvent.getServletContext();

	PAPService.start(servletContext);

	logger.info("PAP service initialization complete!");

    }

    /**
     * {@inheritDoc}
     */
    public void contextDestroyed(ServletContextEvent contextEvent) {

	PAPService.stop();
	logger.info("PAP service shutdown complete!");

    }

}
