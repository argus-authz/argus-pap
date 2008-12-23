package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.AttributeWizard.AttributeWizardType;
import org.opensaml.xacml.policy.PolicyType;

public class UnBanAttribute extends PolicyManagementCLI {
    
    private static String USAGE_DN = "<dn> [options]";
    private static String USAGE_FQAN = "<fqan> [options]";
    private static String[] COMMAND_NAME_VALUES_DN = { "un-ban-user", "ubu" };
    private static String[] COMMAND_NAME_VALUES_FQAN = { "un-ban-fqan", "ubf" };
    private static String DESCRIPTION_DN = "Un-ban a previously banned DN.";
    private static String DESCRIPTION_FQAN = "Un-ban a previously banned FQAN";
    
    public static UnBanAttribute dn() {
        return new UnBanAttribute(COMMAND_NAME_VALUES_DN, USAGE_DN, DESCRIPTION_DN, null, AttributeWizardType.DN);
    }

    public static UnBanAttribute fqan() {
        return new UnBanAttribute(COMMAND_NAME_VALUES_FQAN, USAGE_FQAN, DESCRIPTION_FQAN, null, AttributeWizardType.FQAN);
    }

    private AttributeWizardType attributeToUnBan;
    
    private UnBanAttribute(String[] commandNameValues, String usage, String description,
            String longDescription, AttributeWizardType awt) {
        super(commandNameValues, usage, description, longDescription);
        attributeToUnBan = awt;
    }
    
    @Override
    protected void executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
    
        String[] args = commandLine.getArgs();

        if (args.length != 2)
            throw new ParseException("Wrong number of arguments");
        
        String attributeToUnBanValue = args[1];
        
        initOpenSAML();
        
        List<PolicyType> policyList = policyMgmtClient.listPolicies();
        
        boolean noPoliciesRemoved = true;
        
        for (PolicyType policy:policyList) {
            PolicyWizard pw = new PolicyWizard(policy);
            
            if (AttributeWizardType.DN.equals(attributeToUnBan)) {
                if (pw.isBanPolicyForDN(attributeToUnBanValue)) {
                    removePolicy(pw);
                    noPoliciesRemoved = false;
                }
            } else {
                if (pw.isBanPolicyForFQAN(attributeToUnBanValue)) {
                    removePolicy(pw);
                    noPoliciesRemoved = false;
                }
            }
        }
        
        if (noPoliciesRemoved)
            System.out.println("Error: blacklist policy not found for " + attributeToUnBan.getId() + "=\"" + attributeToUnBanValue + "\"");
        else
            System.out.println("Successfully un-banned " + attributeToUnBan.getId() + "=\"" + attributeToUnBanValue + "\"");
    }
    
    @Override
    protected Options defineCommandOptions() {
        return null;
    }
    
}
