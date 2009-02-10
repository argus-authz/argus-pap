package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;

public class BanAttribute extends PolicyManagementCLI {

    private static String USAGE_DN = "<dn> [options]";
    private static String USAGE_FQAN = "<fqan> [options]";
    private static String[] COMMAND_NAME_VALUES_DN = { "ban-user", "bu" };
    private static String[] COMMAND_NAME_VALUES_FQAN = { "ban-fqan", "bf" };
    private static String DESCRIPTION_DN = "Blacklist a DN on all the resources.";
    private static String DESCRIPTION_FQAN = "Blacklist an FQAN on all the resources.";

    public static BanAttribute dn() {
        return new BanAttribute(COMMAND_NAME_VALUES_DN, USAGE_DN, DESCRIPTION_DN, null, AttributeWizardType.DN);
    }

    public static BanAttribute fqan() {
        return new BanAttribute(COMMAND_NAME_VALUES_FQAN, USAGE_FQAN, DESCRIPTION_FQAN, null, AttributeWizardType.FQAN);
    }

    private AttributeWizardType attributeToDeny;
    
    private BanAttribute(String[] commandNameValues, String usage, String description,
            String longDescription, AttributeWizardType awt) {
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

        if (args.length != 2)
            throw new ParseException("Wrong number of arguments");
        
        String attributeValue = args[1];
        
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
            policyId = highlevelPolicyMgmtClient.banDN(attributeValue, isPublic, policyDescription);
        else
            policyId = highlevelPolicyMgmtClient.banFQAN(attributeValue, isPublic, policyDescription);

        if (verboseMode)
        	System.out.println("ok (id=" + policyId + ")");
        
        return ExitStatus.SUCCESS.ordinal();

    }

}
