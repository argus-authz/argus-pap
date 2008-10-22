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
    
    private static final String OPT_SERVICECLASS_DESCRIPTION = "List only \"serviceclass\" policies.";
    
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
                .withDescription("List only \"private\" policies.").withLongOpt(LOPT_PRIVATE)
                .create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_BLACKLIST_DESCRIPTION)
                .withLongOpt(LOPT_BLACKLIST).create(OPT_BLACKLIST));
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SERVICECLASS_DESCRIPTION)
                .withLongOpt(LOPT_SERVICECLASS).create(OPT_SERVICECLASS));
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
        boolean showBlacklist = true;
        boolean showServiceclass = true;
        boolean xacmlOutput = false;
        boolean plainFormat = false;
        
        if (commandLine.hasOption(LOPT_PRIVATE))
            showPublic = false;
        
        if (commandLine.hasOption(LOPT_PUBLIC))
            showPrivate = false;
        
        if (commandLine.hasOption(OPT_BLACKLIST))
            showServiceclass = false;
        
        if (commandLine.hasOption(OPT_SERVICECLASS))
            showBlacklist = false;
        
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
            policiesFound = listUsingPlaingFormat(policyList,
                    xacmlOutput,
                    showPrivate,
                    showPublic,
                    showBlacklist,
                    showServiceclass);
        else
            policiesFound = listUsingGroupedFormat(policyList,
                    showPrivate,
                    showPublic,
                    showBlacklist,
                    showServiceclass);
        
        if (!policiesFound)
            System.out.println(noPoliciesFoundMessage(showPrivate, showPublic, showBlacklist, showServiceclass));
        
    }
    
    protected static boolean listUsingGroupedFormat(List<PolicyType> policyList,
            boolean showPrivate, boolean showPublic, boolean showBlacklist, boolean showServiceclass) {
        
        boolean somethingHasBeenSelected = false;
        
        LocalPolicySetWizard localPolicySetWizard = new LocalPolicySetWizard();
        
        for (PolicyType policy : policyList) {
            
            try {
                PolicyWizard policyWizard = new PolicyWizard(policy);
                
                if (!policyMustBeShown(policyWizard,
                        showPrivate,
                        showPublic,
                        showBlacklist,
                        showServiceclass))
                    continue;
                
                localPolicySetWizard.addPolicy(policyWizard);
                somethingHasBeenSelected = true;
                
            } catch (UnsupportedPolicyException e) {
                System.out.println("id=" + policy.getPolicyId() + ": "
                        + GENERIC_XACML_ERROR_MESSAGE);
            }
            
        }
        
        localPolicySetWizard.printFormattedBlacklistPolicies(System.out);
        localPolicySetWizard.printFormattedServiceClassPolicies(System.out);
        
        return somethingHasBeenSelected;
    }
    
    protected static boolean listUsingPlaingFormat(List<PolicyType> policyList,
            boolean xacmlOutput, boolean showPrivate, boolean showPublic, boolean showBlacklist,
            boolean showServiceclass) {
        
        boolean somethingHasBeenSelected = false;
        
        for (PolicyType policy : policyList) {
            
            String policyString;
            
            try {
                PolicyWizard policyWizard = new PolicyWizard(policy);
                
                if (!policyMustBeShown(policyWizard,
                        showPrivate,
                        showPublic,
                        showBlacklist,
                        showServiceclass))
                    continue;
                
                if (xacmlOutput)
                    policyString = XMLObjectHelper.toString(policy);
                else
                    policyString = policyWizard.toFormattedString();
                
                System.out.println(policyString);
                somethingHasBeenSelected = true;
                
            } catch (UnsupportedPolicyException e) {
                System.out.println("id=" + policy.getPolicyId() + ": "
                        + GENERIC_XACML_ERROR_MESSAGE);
            }
        }
        return somethingHasBeenSelected;
    }
    
    private static boolean policyMustBeShown(PolicyWizard policyWizard, boolean showPrivate,
            boolean showPublic, boolean showBlacklist, boolean showServiceclass) {
        
        boolean isPrivate = policyWizard.isPrivate();
        boolean isPublic = !isPrivate;
        
        if (!((showPrivate && isPrivate) || (showPublic && isPublic)))
            return false;
        
        boolean isBlacklist = policyWizard.isBlacklistPolicy();
        boolean isServiceclass = policyWizard.isServiceClassPolicy();
        
        if (!((showBlacklist && isBlacklist) || (showServiceclass && isServiceclass)))
            return false;
        
        return true;
    }
    
    protected static String noPoliciesFoundMessage(boolean showPrivate, boolean showPublic,
            boolean showBlacklist, boolean showServiceclass) {

        String visibilityMsg;
        
        if (showPrivate && showPublic)
            visibilityMsg = "";
        else if (showPrivate)
            visibilityMsg = "\"private\"";
        else visibilityMsg = "\"public\"";
        
        String policyWizardTypeMsg;
        
        if (showBlacklist && showServiceclass)
            policyWizardTypeMsg = "";
        else if (showBlacklist)
            policyWizardTypeMsg = "\"blacklist";
        else policyWizardTypeMsg = "\"serviceclass\"";
        
        String msg = "No " + visibilityMsg;
        
        if (visibilityMsg.length() != 0)
            msg += ", ";
        
        msg += policyWizardTypeMsg + " policies has been found.";
        
        return msg;
    }
    
}
