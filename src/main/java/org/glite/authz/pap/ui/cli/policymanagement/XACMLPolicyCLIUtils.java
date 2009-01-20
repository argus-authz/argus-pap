package org.glite.authz.pap.ui.cli.policymanagement;

import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;

public class XACMLPolicyCLIUtils {

    public static void initOpenSAML() {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            throw new PAPConfigurationException("Error initializing OpenSAML library", e);
        }
    }
    
}
