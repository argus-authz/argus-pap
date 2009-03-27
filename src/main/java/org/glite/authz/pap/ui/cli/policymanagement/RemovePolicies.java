package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class RemovePolicies extends PolicyManagementCLI {

    private static String OPT_PURGE_ALL_LONG = "purge-all";
    private static String OPT_PURGE_ALL_DESCRIPTION = "Remove all resources with no actions and all actions with no rules.";
    private static String OPT_PURGE_RESOURCES_LONG = "purge-resources";
    private static String OPT_PURGE_RESOURCES_DESCRIPTION = "Remove all resources with no actions.";
    private static String OPT_PURGE_ACTIONS_LONG = "purge-actions";
    private static String OPT_PURGE_ACTIONS_DESCRIPTION = "Remove all actions with no rules";

    private static final String[] commandNameValues = { "remove-policy", "rp" };
    private static final String DESCRIPTION = "Remove policies (resources and/or actions) by id.";
    private static final String USAGE = "[options] < --purge-* | <<policyId> [[policyId] ...]>>";
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
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_PURGE_ALL_DESCRIPTION)
                                       .withLongOpt(OPT_PURGE_ALL_LONG)
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
        
        if (commandLine.hasOption(OPT_PURGE_ALL_LONG)) {
            purgeResources = true;
            purgeActions = true;
        }
        
        if ((args.length < 2) && !(purgeResources || purgeActions)) {
            throw new ParseException("Wrong number of arguments: an id to remove and/or a purge option must be specified.");
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
            System.out.println("error.");
            throw e;
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

        if (failure && !partialSuccess)
            return ExitStatus.FAILURE.ordinal();

        if (failure && partialSuccess)
            return ExitStatus.PARTIAL_SUCCESS.ordinal();

        return ExitStatus.SUCCESS.ordinal();
    }
}
