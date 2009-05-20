package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedAttributeException;

public class AddPolicy extends PolicyManagementCLI {

    private static String OPT_ACTIONID_LONG = "action-id";
    private static String OPT_ACTIONID_DESCRIPTION = "Specify an action-id.";
    private static String OPT_RULEID_LONG = "rule-id";
    private static String OPT_RULEID_DESCRIPTION = "Specify a rule-id (needs option --" + OPT_ACTIONID_LONG
            + ").";

    private static final String OPT_BOTTOM_LONG = "bottom";
    private static final String OPT_BOTTOM_DESCRIPTION = "Add the policy at the end of the list of rules of the given action.";
    private static final String[] COMMAND_NAME_VALUES = { "add-policy", "ap" };
    private static final String DESCRIPTION = "Add a permit/deny policy.\n"
            + "<permit|deny>  \"permit\" or \"deny\" to add a, respectively, permit/deny policy.\n"
            + "<id=value>     a string in the form \"<id>=<value>\", where <id> is any of the attribute ids that can be specified "
            + "in the simplified policy language and <value> the value to be assigned (e.g. fqan=/vo/group).";
    private static final String LONG_DESCRIPTION = "This command allows to add a rule into an action by specifying "
            + "an action-id (in this case the action must already exist) or a resource/action value (use option --"
            + OPT_ACTIONID_LONG
            + " to specify an action-id or the options --"
            + OPT_RESOURCE_LONG
            + " and --"
            + OPT_ACTION_LONG
            + " to specify resource/action values). "
            + "In the latter case a new resource and/or action are created if they don't already exist. "
            + "By default the rule is inserted at the top of an action unless the --"
            + OPT_BOTTOM_LONG
            + " option is given. If the --"
            + OPT_RULEID_LONG
            + " is set the rule is inserted before the given rule-id or after if the --"
            + OPT_MOVEAFTER_LONG
            + " option is given.";
    private static final String USAGE = "[options] <permit|deny> <id=value>...";
    private String alias = null;

    public AddPolicy() {
        super(COMMAND_NAME_VALUES, USAGE, DESCRIPTION, LONG_DESCRIPTION);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_MOVEAFTER_DESCRIPTION)
                                       .withLongOpt(OPT_MOVEAFTER_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_BOTTOM_DESCRIPTION)
                                       .withLongOpt(OPT_BOTTOM_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_ACTIONID_DESCRIPTION)
                                       .withLongOpt(OPT_ACTIONID_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_RULEID_DESCRIPTION)
                                       .withLongOpt(OPT_RULEID_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_ACTION_DESCRIPTION)
                                       .withLongOpt(OPT_ACTION_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_RESOURCE_DESCRIPTION)
                                       .withLongOpt(OPT_RESOURCE_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length < 3) {
            throw new ParseException("Wrong number of arguments. Usage: " + USAGE);
        }

        boolean isPermit;

        if ("permit".equals(args[1].toLowerCase())) {
            isPermit = true;
        } else if ("deny".equals(args[1].toLowerCase())) {
            isPermit = false;
        } else {
            throw new ParseException("First argument must be \"permit\" or \"deny\" (found: " + args[1] + ")");
        }

        XACMLPolicyCLIUtils.initAttributeWizard();

        List<String> attributeList = new LinkedList<String>();

        for (int i = 2; i < args.length; i++) {

            try {
                new AttributeWizard(args[i]);
                attributeList.add(args[i]);
            } catch (UnsupportedAttributeException e) {
                break;
            }
        }

        if (attributeList.size() == 0) {
            throw new ParseException("Wrong number of arguments. Specify at least one attribute <id=value>");
        }

        String actionId = null;
        String ruleId = null;
        String actionValue = null;
        String resourceValue = null;

        if (commandLine.hasOption(OPT_ACTIONID_LONG)) {
            actionId = commandLine.getOptionValue(OPT_ACTIONID_LONG);
        }

        if (commandLine.hasOption(OPT_RULEID_LONG)) {
            ruleId = commandLine.getOptionValue(OPT_RULEID_LONG);
        }

        if (commandLine.hasOption(OPT_ACTION_LONG)) {
            actionValue = commandLine.getOptionValue(OPT_ACTION_LONG);
        }

        if (commandLine.hasOption(OPT_RESOURCE_LONG)) {
            resourceValue = commandLine.getOptionValue(OPT_RESOURCE_LONG);
        }

        if (actionId == null) {
            if ((resourceValue == null) && (actionValue == null) && (ruleId != null)) {
                throw new ParseException(String.format("Option --%s needs option --%s.",
                                                       OPT_RULEID_LONG,
                                                       OPT_ACTIONID_LONG));
            }
            if ((resourceValue == null) && (actionValue == null)) {
                throw new ParseException(String.format("Specify an action-id or a resource/action value."));
            }
            if ((resourceValue == null) || (actionValue == null)) {
                throw new ParseException(String.format("--%s and --%s must be both present.",
                                                       OPT_RESOURCE_LONG,
                                                       OPT_ACTION_LONG));
            }
            if (ruleId != null) {
                throw new ParseException(String.format("Option --%s needs option --%s and cannot be used with options --%s and --%s.",
                                                       OPT_RULEID_LONG,
                                                       OPT_ACTIONID_LONG,
                                                       OPT_RESOURCE_LONG,
                                                       OPT_ACTION_LONG));
            }
        } else {
            if ((actionValue != null) || (resourceValue != null)) {
                throw new ParseException(String.format("Option --%s cannot be used with options --%s and --%s.",
                                                       OPT_ACTIONID_LONG,
                                                       OPT_RESOURCE_LONG,
                                                       OPT_ACTION_LONG));
            }
        }

        boolean after = false;

        if (commandLine.hasOption(OPT_MOVEAFTER_LONG)) {
            if (ruleId == null) {
                throw new ParseException("The --" + OPT_MOVEAFTER_LONG
                        + " option needs the \"rule-id\" to be specified.");
            }
            after = true;
        }

        if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
        }

        boolean bottom = false;

        if (commandLine.hasOption(OPT_BOTTOM_LONG)) {
            bottom = true;
        }

        if (bottom && (ruleId != null)) {
            throw new ParseException("Specify one of --" + OPT_BOTTOM_LONG
                    + " option or \"rule-id\", not both at the same time.");
        }

        if (bottom) {
            after = true;
        }

        if (verboseMode) {
            System.out.print("Adding policy... ");
        }

        String policyId = null;

        policyId = highlevelPolicyMgmtClient.addRule(alias,
                                                     isPermit,
                                                     attributeList.toArray(new String[attributeList.size()]),
                                                     actionValue,
                                                     resourceValue,
                                                     actionId,
                                                     ruleId,
                                                     after);
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
