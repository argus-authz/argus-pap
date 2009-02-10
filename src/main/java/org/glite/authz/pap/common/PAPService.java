package org.glite.authz.pap.common;

import java.util.Date;

import javax.servlet.ServletContext;

import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;

public final class PAPService {

    static final Logger logger = org.slf4j.LoggerFactory
            .getLogger( PAPService.class );

    protected static void setStartupMonitoringProperties(){
        
        // TODO: find a more reliable naming scheme
        PAPConfiguration.instance().setMonitoringProperty( "ServiceStartupTime", new Date() );
        
        
    }
    public static void start( ServletContext context ) {

        logger.info( "Starting PAP service..." );

        // Initialize configuaration
        PAPConfiguration conf = PAPConfiguration.initialize( context );

        // Start autorization service
        logger.info( "Starting authorization engine..." );

        AuthorizationEngine
                .initialize( conf.getPapAuthzConfigurationFileName() );

        // Bootstrap opensaml
        try {

            logger.info( "Bootstraping OpenSAML..." );
            DefaultBootstrap.bootstrap();

        } catch ( ConfigurationException e ) {

            logger.error( "Error configuring OpenSAML:" + e.getMessage() );
            throw new PAPConfigurationException( "Error configuring OpenSAML:"
                    + e.getMessage(), e );
        }

        // Start repository manager
        logger.info( "Starting repository manager..." );
        RepositoryManager.bootstrap();

        logger.info( "Starting pap distribution module..." );
        DistributionModule.getInstance().startDistributionModule();
        
        setStartupMonitoringProperties();
    }

    public static void stop() {

        logger.info( "Shutting down PAP service..." );

        logger.info( "Shutting down distribution module..." );
        DistributionModule.getInstance().stopDistributionModule();
        
        logger.info( "Shutting down authorization module..." );
        AuthorizationEngine.instance().shutdown();

    }

}
