package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.papmanagement.PAPManagementService;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class PAPManagementCLI extends ServiceCLI {

    protected static final String SERVICE_NAME = "PAPManagementService";
    protected static PAPManagementService papMgmtClient;

    public PAPManagementCLI(String[] commandNameValues, String usageText, String descriptionText) {
        super(commandNameValues, usageText, descriptionText);
    }

    public boolean executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws ParseException, RemoteException {

        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint()
                + SERVICE_NAME);

        return executeCommand(commandLine);
    }

    protected abstract boolean executeCommand(CommandLine commandLine) throws ParseException,
            RemoteException;

}
