package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.glite.authz.pap.ui.cli.CLIException;

public class UnBanAttribute extends PolicyManagementCLI {
    
    private static String[] COMMAND_NAME_VALUES_DN = { "un-ban-user", "ubu" };
    private static String[] COMMAND_NAME_VALUES_FQAN = { "un-ban-fqan", "ubf" };
    private static String DESCRIPTION_DN = "Un-ban a previously banned DN.";
    private static String DESCRIPTION_FQAN = "Un-ban a previously banned FQAN";
    private static String USAGE_DN = "<dn> [options]";
    private static String USAGE_FQAN = "<fqan> [options]";
    
    private AttributeWizardType attributeToUnBan;

    private UnBanAttribute(String[] commandNameValues, String usage, String description,
            String longDescription, AttributeWizardType awt) {
        super(commandNameValues, usage, description, longDescription);
        attributeToUnBan = awt;
    }

    public static UnBanAttribute dn() {
        return new UnBanAttribute(COMMAND_NAME_VALUES_DN, USAGE_DN, DESCRIPTION_DN, null, AttributeWizardType.DN);
    }
    
    public static UnBanAttribute fqan() {
        return new UnBanAttribute(COMMAND_NAME_VALUES_FQAN, USAGE_FQAN, DESCRIPTION_FQAN, null, AttributeWizardType.FQAN);
    }
    
    @Override
    protected Options defineCommandOptions() {
        return null;
    }
    
    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
    
        String[] args = commandLine.getArgs();

        if (args.length != 2)
            throw new ParseException("Wrong number of arguments");
        
        String attributeToUnBanValue = args[1];

        UnbanResult unbanResult;
        
        if (AttributeWizardType.DN.equals(attributeToUnBan))
            unbanResult = highlevelPolicyMgmtClient.unbanDN(attributeToUnBanValue);
        else
            unbanResult = highlevelPolicyMgmtClient.unbanFQAN(attributeToUnBanValue);
        
        if (unbanResult.getStatusCode() != 0) {
            
            System.out.println("Error: ban policy not found for " + attributeToUnBan.getId() + "=\"" + attributeToUnBanValue + "\"");
            return ExitStatus.FAILURE.ordinal();
            
        } else {
            if (verboseMode)
                System.out.println("Successfully un-banned " + attributeToUnBan.getId() + "=\"" + attributeToUnBanValue + "\"");
        }
        
        return ExitStatus.SUCCESS.ordinal();
    }
    
}
