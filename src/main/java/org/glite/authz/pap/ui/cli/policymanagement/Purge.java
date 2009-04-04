package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class Purge extends PolicyManagementCLI {

    private static String OPT_PURGE_RESOURCES_LONG = "only-resources";
    private static String OPT_PURGE_RESOURCES_DESCRIPTION = "Remove resources with no actions.";
    private static String OPT_PURGE_ACTIONS_LONG = "only-actions";
    private static String OPT_PURGE_ACTIONS_DESCRIPTION = "Remove actions with no rules";

    private static final String[] commandNameValues = { "purge"};
    private static final String DESCRIPTION = "Remove resources without actions and actions without rules.";
    private static final String USAGE = "[options]";
    private String alias = null;

    public Purge() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_PURGE_RESOURCES_DESCRIPTION)
                                       .withLongOpt(OPT_PURGE_RESOURCES_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_PURGE_ACTIONS_DESCRIPTION)
                                       .withLongOpt(OPT_PURGE_ACTIONS_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {

        String[] args = commandLine.getArgs();
        
        boolean purgeResources = false;
        boolean purgeActions = false;

        if (commandLine.hasOption(OPT_PURGE_RESOURCES_LONG)) {
            purgeResources = true;
        }
        
        if (commandLine.hasOption(OPT_PURGE_ACTIONS_LONG)) {
            purgeActions = true;
        }
        
        if (!(purgeResources || purgeActions)) {
            purgeActions = true;
            purgeResources = true;
        }
        
        if (args.length > 1) {
            throw new ParseException("Wrong number of arguments: no arguments required.");
        }

        if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
        }

        if (purgeResources && purgeActions) {
            if (verboseMode) {
                System.out.print("Purging actions and resources... ");
            }
            highlevelPolicyMgmtClient.purge(alias, false, true, false, true);
            if (verboseMode) {
                System.out.println("ok.");
            }
        } else if (purgeResources) {
            if (verboseMode) {
                System.out.print("Purging resources... ");
            }
            highlevelPolicyMgmtClient.purge(alias, false, false, false, true);
            if (verboseMode) {
                System.out.println("ok.");
            }
        } else if (purgeActions) {
            if (verboseMode) {
                System.out.print("Purging actions... ");
            }
            highlevelPolicyMgmtClient.purge(alias, false, true, false, false);
            if (verboseMode) {
                System.out.println("ok.");
            }
        }
        return ExitStatus.SUCCESS.ordinal();
    }
}
