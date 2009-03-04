package org.glite.authz.pap.ui.cli.samlclient;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class SAMLClientCLI extends ServiceCLI {

    protected Provisioning samlClient;
    
    public SAMLClientCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }

    @Override
    public int executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {

        samlClient = serviceClient.getProvisioningService(serviceClient.getTargetEndpoint() + serviceClient.getProvisioningServiceName());
        
        return executeCommand(commandLine);
    }

    protected abstract int executeCommand(CommandLine commandLine) throws CLIException,
            ParseException, RemoteException;

}
