package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class Move extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "move", "mv" };
    private static final String DESCRIPTION = "Move a policy (i.e. action <id>) or a policy set (i.e. resource <id>) before/after "
            + "another, respectively, policy or policySet.";
    private static final String LONG_DESCRIPTION = "<id>        the index of the object (action/resouce) to be moved.\n"
            + "<pivotId>   the index of the target object. <id> is moved before <pivotId> by default.\n"
            + "\nAn action can be moved before/after another action of the same resource. "
            + "A resource cannot be moved inside an action.";
    private static final String USAGE = "[options] <id> <pivotId> [-a]";

    private String alias = null;

    public Move() {
        super(commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_MOVEAFTER_DESCRIPTION)
                                       .withLongOpt(OPT_MOVEAFTER_LONG)
                                       .create(OPT_MOVEAFTER));
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create(OPT_PAPALIAS));
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 3) {
            throw new ParseException("Wrong number of arguments");
        }

        if (commandLine.hasOption(OPT_PAPALIAS)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS);
        }

        String id = args[1];
        String pivotId = args[2];

        if (id.equals(pivotId)) {
            return ExitStatus.SUCCESS.ordinal();
        }

        boolean moveAfter = false;

        if (commandLine.hasOption(OPT_MOVEAFTER)) {
            moveAfter = true;
        }

        xacmlPolicyMgmtClient.move(alias, id, pivotId, moveAfter);

        return ExitStatus.SUCCESS.ordinal();
    }
}
