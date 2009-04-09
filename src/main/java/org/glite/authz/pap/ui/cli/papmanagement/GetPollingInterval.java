package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GetPollingInterval extends PAPManagementCLI {

    private static final String[] commandNameValues = { "get-polling-interval", "gpi" };
    private static final String DESCRIPTION = "Get the polling interval in seconds.";
    private static final String USAGE = "";

    public GetPollingInterval() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        float pollingInterval = papMgmtClient.getPollingInterval();

        System.out.println("Polling interval in seconds: " + (long) pollingInterval);
        
        return ExitStatus.SUCCESS.ordinal();
    }
}
