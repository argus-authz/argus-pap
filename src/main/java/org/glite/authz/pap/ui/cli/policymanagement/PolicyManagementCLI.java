package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.policymanagement.PolicyManagementService;
import org.glite.authz.pap.ui.cli.ServiceCLI;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;

public abstract class PolicyManagementCLI extends ServiceCLI {

    protected static final String SERVICE_NAME = "PolicyManagementService";
    protected static PolicyManagementService policyMgmtClient;

    @Override
    protected boolean executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws ParseException, RemoteException {

        policyMgmtClient = serviceClient.getPolicyManagementService(serviceClient.getTargetEndpoint()
                + SERVICE_NAME);

        return executeCommand(commandLine);
    }

    protected abstract boolean executeCommand(CommandLine commandLine) throws ParseException,
            RemoteException;

    protected void initOpenSAML() {
        try {
            DefaultBootstrap.bootstrap();
            XMLConfigurator xmlConfigurator = new XMLConfigurator();

            // Needed because of a "bug" in opensaml 2.1.0... can be removed
            // when opensaml is updated
            xmlConfigurator.load(Configuration.class.getResourceAsStream("/opensaml_bugfix.xml"));
        } catch (ConfigurationException e) {
            throw new PAPConfigurationException("Error initializing OpenSAML library", e);
        }
    }

}
