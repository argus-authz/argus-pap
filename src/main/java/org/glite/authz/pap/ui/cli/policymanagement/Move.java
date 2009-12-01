package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class Move extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "move", "mv" };
    private static final String DESCRIPTION = "Move a resource/action before/after another, respectively, resource/action.";
    private static final String LONG_DESCRIPTION = "Parameters:\n<id>        the index of the object (action/resouce) to be moved.\n"
            + "\nA resource can be moved before/after another resource, an action can be moved before/after another action in the same resource. ";
    private static final String USAGE = "[options] <id> <--" + OPT_BEFORE_ID_LONG + "<id>|--"
            + OPT_AFTER_ID_LONG + "<id>>";

    private String alias = null;

    public Move() {
        super(commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_AFTER_ID_DESCRIPTION)
                                       .withLongOpt(OPT_AFTER_ID_LONG)
                                       .withArgName("id")
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_BEFORE_ID_DESCRIPTION)
                                       .withLongOpt(OPT_BEFORE_ID_LONG)
                                       .withArgName("id")
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 2) {
            throw new ParseException("Wrong number of arguments");
        }

        String id = args[1];

        if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
        }

        String pivotId = null;
        boolean moveAfter = false;

        if (commandLine.hasOption(OPT_BEFORE_ID_LONG)) {
            pivotId = commandLine.getOptionValue(OPT_BEFORE_ID_LONG);
        }

        if (commandLine.hasOption(OPT_AFTER_ID_LONG)) {
            pivotId = commandLine.getOptionValue(OPT_AFTER_ID_LONG);
            moveAfter = true;
        }

        if (pivotId == null) {
            throw new ParseException("One of --" + OPT_BEFORE_ID_LONG + " or --" + OPT_AFTER_ID_LONG
                    + " is required.");
        }

        if (id.equals(pivotId)) {
            return ExitStatus.SUCCESS.ordinal();
        }

        xacmlPolicyMgmtClient.move(alias, id, pivotId, moveAfter);

        return ExitStatus.SUCCESS.ordinal();
    }
}
