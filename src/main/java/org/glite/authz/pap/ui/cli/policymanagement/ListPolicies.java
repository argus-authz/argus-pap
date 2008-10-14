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
import org.opensaml.xacml.policy.PolicyType;

public class ListPolicies extends PolicyManagementCLI {

    private static final String USAGE = "[options]";
    private static final String[] commandNameValues = { "list-policies", "lp" };
    private static final String DESCRIPTION = "List policies authored by the PAP";

    public ListPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
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
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_XACML_DESCRIPTION).withLongOpt(
                LOPT_SHOW_XACML).create());

        return options;
    }

    @Override
    protected void executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        boolean showPrivate = false;
        boolean xacmlOutput = false;

        if (commandLine.hasOption(LOPT_PRIVATE))
            showPrivate = true;

        if (commandLine.hasOption(LOPT_SHOW_XACML))
            xacmlOutput = true;

        initOpenSAML();

        List<PolicyType> policyList = policyMgmtClient.listPolicies();
        
        if (policyList.isEmpty()) {
        	System.out.println("No policies found.");
        	return;
        }
        	

        LocalPolicySetWizard localPolicySetWizard = new LocalPolicySetWizard();
        
        for (PolicyType policy : policyList) {
        	
            PolicyWizard pw = new PolicyWizard(policy);
            
            localPolicySetWizard.addPolicy(pw);
            
            
            if (showPrivate == pw.isPrivate()) {
                if (xacmlOutput)
                    System.out.println(XMLObjectHelper.toString(pw.getPolicyType()));
                else
                    System.out.println(pw.toFormattedString());
            }
        }
        System.out.println("*******************************************************");
        localPolicySetWizard.printFormattedBlacklistPolicies(System.out);
        
        localPolicySetWizard.printFormattedServiceClassPolicies(System.out);

    }

}
