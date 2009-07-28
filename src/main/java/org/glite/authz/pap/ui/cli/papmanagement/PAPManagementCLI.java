package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class PAPManagementCLI extends ServiceCLI {

    protected static String OPT_LOCAL = "l";
    protected static String OPT_LOCAL_LONG = "local";
    protected static String OPT_LOCAL_DESCRIPTION = "Set the pap as local.";
    protected static String OPT_REMOTEL_LONG = "remote";
    protected static String OPT_REMOTE_DESCRIPTION = "Set the pap as remote.";
    protected static String OPT_NO_POLICIES_LONG = "no-policies";
    protected static String OPT_NO_POLICIES_DESCRIPTION = "Do not fetch the policies now.";
    protected PAPManagement papMgmtClient;
    protected ServiceClient serviceClient;

    public PAPManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }

    @Override
    public int executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {

        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint()
                + serviceClient.getPAPManagementServiceName());

        this.serviceClient = serviceClient;

        return executeCommand(commandLine);
    }

    protected abstract int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException;
}
