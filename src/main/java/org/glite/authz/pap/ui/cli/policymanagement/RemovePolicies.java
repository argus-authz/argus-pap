package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class RemovePolicies extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "remove-policy", "rp" };
    private static final String DESCRIPTION = "Remove policies (resources and/or actions) by id.";
    private static final String USAGE = "[options] <policyId> [[policyId] ...]";
    private String alias = null;

    public RemovePolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create(OPT_PAPALIAS));
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length < 2) {
            throw new ParseException("Missing argument <policyId>");
        }

        if (commandLine.hasOption(OPT_PAPALIAS)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS);
        }

        boolean partialSuccess = false;
        boolean failure = false;

        try {

            for (int i = 1; i < args.length; i++) {

                String policyId = args[i];

                if (verboseMode) {
                    System.out.print("Removing \"" + policyId + "\"... ");
                }

                boolean policyRemoved = xacmlPolicyMgmtClient.removeObjectByIdAndReferences(alias, policyId);

                if (!policyRemoved) {
                    if (verboseMode) {
                        System.out.println("error: not found.");
                    } else {
                        System.out.println("Error id not found: " + policyId);
                    }
                    failure = true;
                    continue;
                }

                partialSuccess = true;

                if (verboseMode) {
                    System.out.println("ok.");
                }
            }
        } catch (RemoteException e) {
            System.out.println("ERROR.");
            throw e;
        }

        if (failure && !partialSuccess)
            return ExitStatus.FAILURE.ordinal();

        if (failure && partialSuccess)
            return ExitStatus.PARTIAL_SUCCESS.ordinal();

        return ExitStatus.SUCCESS.ordinal();

    }

}
