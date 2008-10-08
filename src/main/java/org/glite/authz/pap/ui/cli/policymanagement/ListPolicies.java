package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.opensaml.xacml.policy.PolicyType;

public class ListPolicies extends PolicyManagementCLI {

    private static final String LOPT_PUBLIC = "public";
    private static final String LOPT_PRIVATE = "private";
    private static final String LOPT_SHOW_XACML = "show-xacml";

    private static final String USAGE = "<alias> <endpoint> <dn> [options]";
    private static final String[] commandNameValues = { "list-policies", "lp" };
    private static final String DESCRIPTION = "List policies authored by the PAP";

    @Override
    protected String[] defineCommandNameValues() {
        return commandNameValues;
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
        options.addOption(OptionBuilder.hasArg(false).withDescription(
                "XACML ").withLongOpt(LOPT_PRIVATE)
                .create());

        return options;
    }

    @Override
    protected String defineDescription() {
        return DESCRIPTION;
    }

    @Override
    protected String defineLongDescription() {
        return null;
    }

    @Override
    protected String defineUsage() {
        return USAGE;
    }

    @Override
    protected boolean executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        boolean showPrivate = false;
        boolean xacmlOutput = false;

        if (commandLine.hasOption(LOPT_PRIVATE))
            showPrivate = true;
        
        if (commandLine.hasOption(LOPT_SHOW_XACML))
            xacmlOutput = true;
        
        initOpenSAML();
        
        List<PolicyType> policyList = policyMgmtClient.listPolicies();
        
        for (PolicyType policy:policyList) {
            PolicyWizard pw = new PolicyWizard(policy);
            if (xacmlOutput)
                System.out.println(XMLObjectHelper.toString(pw.getPolicyType()));
            else
                System.out.println(pw.toFormattedString());
        }
        
        return true;
    }

}
