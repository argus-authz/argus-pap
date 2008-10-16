package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.papmanagement.PAPManagementService;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class PAPManagementCLI extends ServiceCLI {

    protected static final String SERVICE_NAME = "PAPManagementService";
    protected PAPManagementService papMgmtClient;
    
    public PAPManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }

    @Override
    public void executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {

        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint()
                + SERVICE_NAME);

        executeCommand(commandLine);
    }

    protected abstract void executeCommand(CommandLine commandLine) throws CLIException,
            ParseException, RemoteException;

}
