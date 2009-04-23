package org.glite.authz.pap.authz;

import java.io.File;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.glite.authz.pap.authz.exceptions.PAPAuthzException;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.voms.PKIStore;
import org.glite.voms.PKIVerifier;
import org.glite.voms.VOMSValidator;
import org.glite.voms.ac.ACValidator;
import org.mortbay.log.Log;
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
