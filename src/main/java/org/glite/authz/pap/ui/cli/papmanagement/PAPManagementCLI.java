package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class PAPManagementCLI extends ServiceCLI {

    protected PAPManagement papMgmtClient;
    
    public PAPManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }

    @Override
    public int executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {

        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint()
                + serviceClient.getPAPManagementServiceName());

        return executeCommand(commandLine);
    }

    protected abstract int executeCommand(CommandLine commandLine) throws CLIException,
            ParseException, RemoteException;

}
