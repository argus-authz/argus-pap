package org.glite.authz.pap.common;

import javax.servlet.ServletContext;

import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;
import org.slf4j.Logger;

public final class PAPService {

    static final Logger logger = org.slf4j.LoggerFactory
	    .getLogger(PAPService.class);

    public static void start(ServletContext context) {

	logger.info("Starting PAP service...");

	// Initialize configuaration
	PAPConfiguration conf = PAPConfiguration.initialize(context);

	// Start autorization service
	logger.info("Starting authorization engine...");

	AuthorizationEngine.initialize(conf.getPapAuthzConfigurationFileName());

	// Bootstrap opensaml
	try {

	    logger.info("Bootstraping OpenSAML...");
	    DefaultBootstrap.bootstrap();
	    XMLConfigurator xmlConfigurator = new XMLConfigurator();

	    // Needed because of a "bug" in opensaml 2.1.0... can be removed
	    // when opensaml is updated
	    xmlConfigurator.load(Configuration.class
		    .getResourceAsStream("/opensaml_bugfix.xml"));

	} catch (ConfigurationException e) {

	    logger.error("Error configuring OpenSAML:" + e.getMessage());
	    throw new PAPConfigurationException("Error configuring OpenSAML:"
		    + e.getMessage(), e);
	}

	// Start repository manager
	logger.info("Starting repository manager...");
	RepositoryManager.getInstance().bootstrap();
	
	logger.info("Starting pap distribution module...");
	DistributionModule.getInstance().startDistributionModule();    }

    public static void stop() {

	logger.info("Shutting down PAP service...");
	
	logger.info("Shutting down distribution module...");
	DistributionModule.getInstance().stopDistributionModule();
	
    }

}
