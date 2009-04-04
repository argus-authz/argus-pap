package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class AddPolicy extends PolicyManagementCLI {

    private static String[] COMMAND_NAME_VALUES = { "add-policy", "ap" };
    private static String DESCRIPTION = "Add a permit/deny policy.\n" +
    		"<permit|deny>  \"permit\" or \"deny\" to add a, respectively, permit/deny policy.\n" +
    		"<id>           any of the attribute ids that can be specified in the simplified policy language.\n" +
    		"<value>        value of the attribute identified by \"id\"\n" +
    		"<action-id>    the policy is inserted into the action with id=<action-id>\n" +
    		"[rule-id]      if specified the policy is added before \"rule-id\" (after if --" + OPT_MOVEAFTER_LONG + " is set).";
    private static String USAGE = "[options] <permit|deny> <id> <value> <action-id> [rule-id]";
    private String alias = null;

    public AddPolicy() {
        super(COMMAND_NAME_VALUES, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_MOVEAFTER_DESCRIPTION)
                                       .withLongOpt(OPT_MOVEAFTER_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if ((args.length < 5) || (args.length > 6)) {
            throw new ParseException("Wrong number of arguments");
        }

        boolean isPermit;
        
        if ("permit".equals(args[1].toLowerCase())) {
            isPermit = true;
        } else if ("deny".equals(args[1].toLowerCase())) {
            isPermit = false;
        } else {
            throw new ParseException("First argument must be \"permit\" or \"deny\" (found: " + args[1] + ")");
        }
        
        String id = args[2];
        String value = args[3];
        String actionId = args[4];
        String ruleId = null;
        boolean moveAfter = false;
        
        if (args.length == 6) {
            ruleId = args[5];
        }
        
        if (commandLine.hasOption(OPT_MOVEAFTER_LONG)) {
            moveAfter = true;
        }
        
        if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
        }

        if (verboseMode) {
            System.out.print("Adding policy... ");
        }

        String policyId = null;

        policyId = highlevelPolicyMgmtClient.addRule(alias, isPermit, id, value, actionId, ruleId, moveAfter);

        if (policyId == null) {
            printOutputMessage(String.format("error."));
        } else {
            if (verboseMode) {
                System.out.println("ok");
            }
        }
        return ExitStatus.SUCCESS.ordinal();
    }
}
