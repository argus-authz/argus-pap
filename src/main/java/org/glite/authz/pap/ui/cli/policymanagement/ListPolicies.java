package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.ui.wizard.LocalPolicySetWizard;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.UnsupportedPolicyException;
import org.opensaml.xacml.policy.PolicyType;

public class ListPolicies extends PolicyManagementCLI {
    
    private static final String USAGE = "[options]";
    private static final String[] commandNameValues = { "list-policies", "lp" };
    private static final String DESCRIPTION = "List policies authored by the PAP."; 
    
    public ListPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        
        options.addOption(OptionBuilder.hasArg(false)
                .withDescription("List only \"public\" policies.").withLongOpt(LOPT_PUBLIC)
                .create());
        options.addOption(OptionBuilder.hasArg(false)
                .withDescription("List only \"private\" policies.")
                .withLongOpt(LOPT_PRIVATE).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_XACML_DESCRIPTION)
                .withLongOpt(LOPT_SHOW_XACML).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_PLAIN_FORMAT_DESCRIPTION)
                .withLongOpt(LOPT_PLAIN_FORMAT).create());
        
        return options;
    }
    
    @Override
    protected void executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        boolean showPrivate = true;
        boolean showPublic = true;
        boolean xacmlOutput = false;
        boolean plainFormat = false;
        
        if (commandLine.hasOption(LOPT_PRIVATE))
            showPublic = false;
        if (commandLine.hasOption(LOPT_PUBLIC))
            showPrivate = false;
        if (commandLine.hasOption(LOPT_SHOW_XACML))
            xacmlOutput = true;
        if (commandLine.hasOption(LOPT_PLAIN_FORMAT))
            plainFormat = true;
        
        initOpenSAML();
        
        List<PolicyType> policyList = policyMgmtClient.listPolicies();
        
        if (policyList.isEmpty()) {
            System.out.println("No policies has been found.");
            return;
        }
        
        boolean policiesFound;
        
        if (plainFormat || xacmlOutput)
            policiesFound = listUsingPlaingFormat(policyList, xacmlOutput, showPrivate, showPublic);
        else
            policiesFound = listUsingGroupedFormat(policyList, showPrivate, showPublic);
        
        if (!policiesFound) {
            String requestedVisibility = "PUBLIC";
            
            if (showPrivate)
                requestedVisibility = "PRIVATE";
            
            System.out.println("No " + requestedVisibility +  " policies has been found.");
        }
        
    }
    
    protected static boolean listUsingGroupedFormat(List<PolicyType> policyList, boolean showPrivate,
            boolean showPublic) {
        
        boolean somethingHasBeenWritten = false;
        
        LocalPolicySetWizard localPolicySetWizard = new LocalPolicySetWizard();
        
        for (PolicyType policy : policyList) {
            
            try {
                PolicyWizard policyWizard = new PolicyWizard(policy);
                
                boolean isPrivate = policyWizard.isPrivate();
                
                if ((showPrivate && isPrivate) || (showPublic && !isPrivate)) {
                    localPolicySetWizard.addPolicy(policyWizard);
                    somethingHasBeenWritten = true;
                }
                
            } catch (UnsupportedPolicyException e) {
                System.out.println("id=" + policy.getPolicyId() + ": "
                        + GENERIC_XACML_ERROR_MESSAGE);
            }
            
        }
        
        localPolicySetWizard.printFormattedBlacklistPolicies(System.out);
        localPolicySetWizard.printFormattedServiceClassPolicies(System.out);
        
        return somethingHasBeenWritten;
    }
    
    protected static boolean listUsingPlaingFormat(List<PolicyType> policyList,
            boolean xacmlOutput, boolean showPrivate, boolean showPublic) {
        
        boolean somethingHasBeenWritten = false;
        
        for (PolicyType policy : policyList) {
            
            String policyString;
            
            try {
                PolicyWizard policyWizard = new PolicyWizard(policy);
                
                boolean isPrivate = policyWizard.isPrivate();
                
                if ((showPrivate && isPrivate) || (showPublic && !isPrivate)) {
                    
                    if (xacmlOutput)
                        policyString = XMLObjectHelper.toString(policy);
                    else
                        policyString = policyWizard.toFormattedString();
                    
                    System.out.println(policyString);
                    somethingHasBeenWritten = true;
                }
                
            } catch (UnsupportedPolicyException e) {
                System.out.println("id=" + policy.getPolicyId() + ": "
                        + GENERIC_XACML_ERROR_MESSAGE);
            }
        }
        return somethingHasBeenWritten;
    }
}
