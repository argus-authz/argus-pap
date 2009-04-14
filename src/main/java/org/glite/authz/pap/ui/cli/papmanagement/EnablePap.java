package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class EnablePap extends PAPManagementCLI {

    private static final String[] commandNameValues = { "enable-pap", "epap" };
    private static final String DESCRIPTION = "Set a pap as enabled (i.e. PDPs will get its policies).";
    private static final String USAGE = "<alias>";

    public EnablePap() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if (args.length != 2) {
            throw new ParseException("Wrong number of arguments");
        }

        String alias = args[1];

        if (!papMgmtClient.exists(alias)) {
            System.out.println("PAP not found: " + alias);
            return ExitStatus.FAILURE.ordinal();
        }

        papMgmtClient.setEnabled(alias, true);

        return ExitStatus.SUCCESS.ordinal();

    }

}
