package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RemoveAllPolicies extends PolicyManagementCLI {

    private static final String USAGE = "[options]";
    private static final String[] commandNameValues = { "remove-all-policies", "rap" };
    private static final String DESCRIPTION = "Delete all policies.";

    public RemoveAllPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        if (verboseMode) {
            System.out.print("Removing all policies... ");
        }

        highlevelPolicyMgmtClient.eraseRepository();

        if (verboseMode)
            System.out.println("ok");

        return ExitStatus.SUCCESS.ordinal();
    }
}
