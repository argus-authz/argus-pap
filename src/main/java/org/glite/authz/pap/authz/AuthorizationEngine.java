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

package org.glite.authz.pap.authz;

import java.io.File;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class bootstraps the PAP authorization engine
 *
 */
public class AuthorizationEngine {
	
    
    public static final Logger logger = LoggerFactory.getLogger( AuthorizationEngine.class );

    /** A flag static whether the authz engine has been initialized **/
    private boolean initialized = false;

    /** The singleton instance for the AuthorizationEngine class **/
    private static AuthorizationEngine instance;

    /** The global context to which all ACLs are currently mapped. **/
    private PAPContext globalContext;

    /**
     * Constructor 
     * @param papAuthzConfFile, the configuration file used to initialize the PAP authorization engine  
     */
    private AuthorizationEngine( String papAuthzConfFile ) {

        File papConfFile = new File( papAuthzConfFile );

        if ( !papConfFile.exists() )
            throw new PAPConfigurationException(
                    "PAP Authorization configuration file not found: "
                            + papConfFile.getAbsolutePath() );

        globalContext = PAPContext.instance( "global-context" );

        // Parse ACL from configuration file
        AuthzConfigurationParser confParser = AuthzConfigurationParser
                .instance();

        confParser.parse( papConfFile );

        globalContext.setAcl( confParser.getParsedACL() );
        
    }

    /**
     * Initializes the PAP authorization engine
     * 
     * @param papAuthzConfFile, the configuration file used to initialize the PAP authorization engine
     * @return
     */
    public static AuthorizationEngine initialize( String papAuthzConfFile ) {

        if ( instance == null )
            instance = new AuthorizationEngine( papAuthzConfFile );

        return instance;
    }

    /**
     * Returns an instance of the AuthorizationEngine 
     */
    public static AuthorizationEngine instance() {

        if ( instance == null )
            logger.warn( "Authorization engine hasn't been properly initialized!" );
            

        return instance;
    }

    /**
     * Saves the authorization engine status back to the authz engine configuration file
     */
    public void saveConfiguration() {

        String confFileName = PAPConfiguration.instance()
                .getPapAuthzConfigurationFileName();

        AuthzConfigurationParser confParser = AuthzConfigurationParser
                .instance();

        confParser.save( new File( confFileName ), getGlobalContext().getAcl() );
    }

    /**
     * Returns the status of the initialization flag for the AuthorizationEngine 
     */
    public boolean isInitialized() {

        return initialized;
    }

    /**
     * Returns the global context for the authorization engine
     * @return a {@link PAPContext} object for the global context
     */
    public PAPContext getGlobalContext() {

        return globalContext;
    }
    
    /**
     * Performs some cleanup for the service shutdown.
     */
    public void shutdown(){
        
        if (CurrentAdmin.validator != null)
            // Cleanup VOMS lib - this will not be required after future voms api refactoring
            CurrentAdmin.validator.cleanup();
    }

}
