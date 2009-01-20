package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.wizard.AttributeWizard.AttributeWizardType;

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

        options.addOption(OptionBuilder.hasArg(false).withDescription(
                "Set the policy as public (default)").withLongOpt(LOPT_PUBLIC).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(
                "Set the policy as private (it won't be distributed)").withLongOpt(LOPT_PRIVATE)
                .create());

        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 2)
            throw new ParseException("Wrong number of arguments");
        
        String attributeValue = args[1];
        
        boolean isPrivate = false;
        if (commandLine.hasOption(LOPT_PRIVATE))
            isPrivate = true;

        if (verboseMode)
            System.out.print("Adding policy... ");
        
        String policyId;
        
        if (AttributeWizardType.DN.equals(attributeToDeny))
            policyId = highlevelPolicyMgmtClient.banDN(attributeValue, !isPrivate);
        else
            policyId = highlevelPolicyMgmtClient.banFQAN(attributeValue, !isPrivate);

        if (verboseMode)
        	System.out.println("ok (id=" + policyId + ")");
        
        return ExitStatus.SUCCESS.ordinal();

    }

}