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

package org.glite.authz.pap.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Valerio Venturi (valerio.venturi@cnaf.infn.it)
 *
 */
public class PAPContextListener implements ServletContextListener
{
  final Logger logger = LoggerFactory.getLogger(PAPContextListener.class);
  
  public PAPContextListener() {
    super();
  }

  public void contextInitialized(ServletContextEvent contextEvent) {

    // get the servlet context
    ServletContext servletContext = contextEvent.getServletContext();
   
    /* get the value of the ConfigurationFile param in the context.xml file */
    
    String configurationFile = servletContext.getInitParameter("ConfigurationFile");
    
    if(configurationFile == null) {
      logger.error( "Configuration file not given" );
      throw new Error( "Configuration file not given" );
    }
    
    logger.info("Using configuration file " + configurationFile);
    
    /* instantiate the configuration object */
    
    try {
      PAPConfiguration.getInstance(configurationFile);
    } catch ( PAPConfigurationException e ) {
      logger.error( e.getMessage() );
      throw new Error(e);
    }
    
  }

  public void contextDestroyed(ServletContextEvent contextEvent) {
  }
  
}
