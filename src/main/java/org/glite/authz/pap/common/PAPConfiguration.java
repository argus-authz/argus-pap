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

 File : PAPConfiguration.java

 Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPConfiguration {

    final static Logger logger = LoggerFactory
            .getLogger( PAPConfiguration.class );

    private final static PAPConfiguration instance = new PAPConfiguration();

    private static Properties properties = new Properties();

    private PAPConfiguration() {

    }

    public static PAPConfiguration getInstance( String configurationFileName ) {

        /* load properties from file */

        try {
            loadConfigurationProperties( configurationFileName );
        } catch ( IOException e ) {
            throw new PAPConfigurationException( e );
        }

        /* configure OpenSAML */

        try {
            configureOpenSAML();
        } catch ( ConfigurationException e ) {
            throw new PAPConfigurationException( e );
        }

        return instance;
    }

    private static void loadConfigurationProperties(
            String configurationFileName ) throws IOException {

        FileInputStream inputStream = new FileInputStream(
                configurationFileName );

        properties.load( inputStream );

    }

    public static PAPConfiguration getInstance() {

        return instance;
    }

    public String getPolicyFile() {

        return properties.getProperty( "policyFile" );
    }
    
    public static void bootstrap() throws ConfigurationException {
    	configureOpenSAML();
    	configureRepository();
    }
    
    private static void configureRepository() {
    	RepositoryManager.getInstance().bootstrap();
    }

    public static void configureOpenSAML() throws ConfigurationException {

        DefaultBootstrap.bootstrap();
        XMLConfigurator xmlConfigurator = new XMLConfigurator();
        
        // Needed because of a "bug" in opensaml 2.1.0... can be removed when opensaml is updated
		xmlConfigurator.load( Configuration.class.getResourceAsStream( "/opensaml_bugfix.xml" ) );

    }

}
