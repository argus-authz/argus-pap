package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.ui.cli.CLIException;

public class Ping extends PAPManagementCLI {

    private static final String[] commandNameValues = { "ping" };
    private static final String DESCRIPTION = "Ping a PAP (default endpoint is: "
            + String.format(DEFAULT_SERVICE_URL,
                            Pap.DEFAULT_HOST,
                            Pap.DEFAULT_PORT,
                            Pap.DEFAULT_SERVICES_ROOT_PATH) + ").";
    private static final String USAGE = "";

    public Ping() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length > 1)
            throw new ParseException("Wrong number of arguments");
        
        System.out.print("Contacting PAP at \"" + serviceClient.getTargetEndpoint() + "\"... ");

        String papVersion = papMgmtClient.ping();

        System.out.println("ok (" + papVersion + ")");

        return ExitStatus.SUCCESS.ordinal();
    }
}
