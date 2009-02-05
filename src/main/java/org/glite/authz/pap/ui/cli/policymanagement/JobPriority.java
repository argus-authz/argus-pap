package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.wizard.AttributeWizard.AttributeWizardType;

public class JobPriority extends PolicyManagementCLI {

    private static String USAGE_DN = "<dn> <service_class> [options]";
    private static String USAGE_FQAN = "<fqan> <service_class> [options]";
    private static String[] COMMAND_NAME_VALUES_DN = { "user-job-prority", "ujp" };
    private static String[] COMMAND_NAME_VALUES_FQAN = { "fqan-job-priority", "fjp" };
    private static String DESCRIPTION_DN = "Assign a user to a service class.";
    private static String DESCRIPTION_FQAN = "Assign an fqan to a service class";

    public static JobPriority dn() {
        return new JobPriority(COMMAND_NAME_VALUES_DN, USAGE_DN, DESCRIPTION_DN, null, AttributeWizardType.DN);
    }

    public static JobPriority fqan() {
        return new JobPriority(COMMAND_NAME_VALUES_FQAN, USAGE_FQAN, DESCRIPTION_FQAN, null, AttributeWizardType.FQAN);
    }

    private AttributeWizardType attributeToDeny;

    private JobPriority(String[] commandNameValues, String usage, String description, String longDescription,
            AttributeWizardType awt) {

        super(commandNameValues, usage, description, longDescription);
        attributeToDeny = awt;
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();

        options.addOption(OptionBuilder.hasArg(false).withDescription("Set the policy as public (default)").withLongOpt(
                OPT_PUBLIC_LONG).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription("Set the policy as private (it won't be distributed)")
                .withLongOpt(OPT_PRIVATE_LONG).create());
        options.addOption(OptionBuilder.hasArg().withDescription(OPT_POLICY_DESCRIPTION_DESCRIPTION).withLongOpt(
                OPT_POLICY_DESCRIPTION_LONG).create(OPT_POLICY_DESCRIPTION));

        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if (args.length != 3)
            throw new ParseException("Wrong number of arguments");

        String attributeValue = args[1];
        String serviceClass = args[2];

        boolean isPublic = true;
        if (commandLine.hasOption(OPT_PRIVATE_LONG))
            isPublic = false;
        
        String policyDescription = null;
        if (commandLine.hasOption(OPT_POLICY_DESCRIPTION_LONG))
            policyDescription = commandLine.getOptionValue(OPT_POLICY_DESCRIPTION_LONG);

        if (verboseMode)
            System.out.print("Adding policy... ");

        String policyId;

        if (AttributeWizardType.DN.equals(attributeToDeny))
            policyId = highlevelPolicyMgmtClient.dnJobPriority(attributeValue, serviceClass, isPublic, policyDescription);
        else
            policyId = highlevelPolicyMgmtClient.fqanJobPriority(attributeValue, serviceClass, isPublic, policyDescription);

        if (verboseMode)
            System.out.println("ok (id=" + policyId + ")");

        return ExitStatus.SUCCESS.ordinal();

    }

}
