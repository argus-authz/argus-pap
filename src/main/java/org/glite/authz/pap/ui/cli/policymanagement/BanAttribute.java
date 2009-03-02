package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;

public class BanAttribute extends PolicyManagementCLI {

    private static String[] COMMAND_NAME_VALUES_DN = { "ban-user", "bu" };
    private static String[] COMMAND_NAME_VALUES_FQAN = { "ban-fqan", "bf" };
    private static String DESCRIPTION_DN = "Ban a DN.";
    private static String DESCRIPTION_FQAN = "Ban an FQAN.";
    private static String OPT_ACTION = "a";
    private static String OPT_ACTION_DESCRIPTION = "Specify an action value.";
    private static String OPT_ACTION_LONG = "action";
    private static String OPT_RESOURCE = "r";

    private static String OPT_RESOURCE_DESCRIPTION = "Specify a resource value.";
    private static String OPT_RESOURCE_LONG = "resource";
    private static String USAGE_DN = "<dn> [options]";
    private static String USAGE_FQAN = "<fqan> [options]";

    private AttributeWizardType attributeToDeny;

    private BanAttribute(String[] commandNameValues, String usage, String description, String longDescription,
            AttributeWizardType awt) {
        super(commandNameValues, usage, description, longDescription);
        attributeToDeny = awt;
    }

    public static BanAttribute dn() {
        return new BanAttribute(COMMAND_NAME_VALUES_DN, USAGE_DN, DESCRIPTION_DN, null, AttributeWizardType.DN);
    }

    public static BanAttribute fqan() {
        return new BanAttribute(COMMAND_NAME_VALUES_FQAN, USAGE_FQAN, DESCRIPTION_FQAN, null, AttributeWizardType.FQAN);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();

        options.addOption(OptionBuilder.hasArg(false).withDescription("Set the policy as public (default)").withLongOpt(
                OPT_PUBLIC_LONG).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription("Set the policy as private (it won't be distributed)")
                .withLongOpt(OPT_PRIVATE_LONG).create());
        options.addOption(OptionBuilder.hasArg().withDescription(OPT_ACTION_DESCRIPTION).withLongOpt(OPT_ACTION_LONG).create(OPT_ACTION));
        options.addOption(OptionBuilder.hasArg().withDescription(OPT_RESOURCE_DESCRIPTION).withLongOpt(OPT_RESOURCE_LONG).create(
                OPT_RESOURCE));

        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 2)
            throw new ParseException("Wrong number of arguments");

        String attributeValue = args[1];

        boolean isPublic = true;
        if (commandLine.hasOption(OPT_PRIVATE_LONG))
            isPublic = false;

        String resource = null;
        String action = null;
        
        if (commandLine.hasOption(OPT_RESOURCE)) {
            resource = commandLine.getOptionValue(OPT_RESOURCE);
        } else {
            resource = "*";
        }
        
        if (commandLine.hasOption(OPT_ACTION)) {
            action = commandLine.getOptionValue(OPT_ACTION);
        } else {
            action = "*";
        }

        if (verboseMode) {
            System.out.print("Adding policy... ");
        }

        String policyId = null;

        if (AttributeWizardType.DN.equals(attributeToDeny)) {
            policyId = highlevelPolicyMgmtClient.banDN(attributeValue, resource, action, isPublic);
        } else {
            policyId = highlevelPolicyMgmtClient.banFQAN(attributeValue, resource, action, isPublic);
        }

        if (policyId == null) {
            printOutputMessage(String.format("ban rule already exists"));
        } else {
            if (verboseMode) {
                System.out.println("ok (id=" + policyId + ")");
            }
        }

        return ExitStatus.SUCCESS.ordinal();
    }

}

