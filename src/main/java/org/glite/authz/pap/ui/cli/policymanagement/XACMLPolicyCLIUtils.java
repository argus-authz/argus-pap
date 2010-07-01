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
