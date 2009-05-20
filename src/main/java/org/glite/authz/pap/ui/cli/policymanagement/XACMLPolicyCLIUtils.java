package org.glite.authz.pap.ui.cli.policymanagement;

import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizardTypeConfiguration;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;

public class XACMLPolicyCLIUtils {

    private static boolean notInitilized = true;

    public static void initOpenSAMLAndAttributeWizard() {

        if (notInitilized) {
            
            initAttributeWizard();
            
            try {
                DefaultBootstrap.bootstrap();
                notInitilized = false;
            } catch (ConfigurationException e) {
                throw new PAPConfigurationException("Error initializing OpenSAML library", e);
            }
        }
    }

    public static void initAttributeWizard() {
        String papDir = System.getProperty("PAP_HOME");

        if (papDir == null) {
            throw new CLIException("Environment variable PAP_HOME is not set.");
        }

        AttributeWizardTypeConfiguration.bootstrap(papDir + "/conf/attribute-mappings.ini");
    }

}
