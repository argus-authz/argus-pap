package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class ListPAPPolicies extends PolicyManagementCLI {
    
    private static final String USAGE = "<pap_alias> [options]";
    private static final String[] commandNameValues = { "list-pap-policies", "lpp" };
    private static final String DESCRIPTION = "List cached policies of the remote PAP \"pap_alias\". ";
    
    public ListPAPPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
        
        String[] args = commandLine.getArgs();
        
        if (args.length != 2)
            throw new ParseException("Missing argument <pap_alias>");
        
        String papAlias = args[1];
        
        boolean xacmlOutput = false;
        boolean plainFormat = false;
        boolean showBlacklist = true;
        boolean showServiceclass = true;
        boolean getPoliciesOneByOne = false;
        
        if (commandLine.hasOption(OPT_SHOW_XACML_LONG))
            xacmlOutput = true;
        
        if (commandLine.hasOption(OPT_PLAIN_FORMAT_LONG))
            plainFormat = true;
        
        if (commandLine.hasOption(OPT_BLACKLIST))
            showServiceclass = false;
        
        if (commandLine.hasOption(OPT_SERVICECLASS))
            showBlacklist = false;
        
        if (commandLine.hasOption(OPT_LIST_ONE_BY_ONE))
            getPoliciesOneByOne = true;
        
        XACMLPolicyCLIUtils.initOpenSAML();
        
        PAPPolicyIterator policyIter = new PAPPolicyIterator(xacmlPolicyMgmtClient, papAlias, !getPoliciesOneByOne);
        
        policyIter.init();
        
        if (policyIter.getNumberOfPolicies() == 0) {
            System.out.println("No policies has been found.");
            return ExitStatus.SUCCESS.ordinal();
        }
        
        boolean policiesFound;
        
        if (xacmlOutput || plainFormat)
            policiesFound = ListPolicies.listUsingPlaingFormat(policyIter, xacmlOutput, true, true, showBlacklist, showServiceclass);
        else
            policiesFound = ListPolicies.listUsingGroupedFormat(policyIter, true, true, showBlacklist, showServiceclass, false);
        
        if (!policiesFound)
            System.out.println(ListPolicies.noPoliciesFoundMessage(true, true, showBlacklist, showServiceclass));
        
        return ExitStatus.SUCCESS.ordinal();
        
    }
    
    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_XACML_DESCRIPTION)
                .withLongOpt(OPT_SHOW_XACML_LONG).create());
        
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_PLAIN_FORMAT_DESCRIPTION)
                .withLongOpt(OPT_PLAIN_FORMAT_LONG).create());
        
        return options;
    }
    
}
