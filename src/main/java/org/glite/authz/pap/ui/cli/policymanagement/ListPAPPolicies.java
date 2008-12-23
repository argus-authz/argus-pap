package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicyType;

public class ListPAPPolicies extends PolicyManagementCLI {
    
    private static final String USAGE = "<papId> [options]";
    private static final String[] commandNameValues = { "list-pap-policies", "lpp" };
    private static final String DESCRIPTION = "List cached policies of the remote PAP \"papId\". ";
    
    public ListPAPPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    protected void executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
        
        String[] args = commandLine.getArgs();
        
        if (args.length != 2)
            throw new ParseException("Missing argument <papId>");
        
        boolean xacmlOutput = false;
        boolean plainFormat = false;
        boolean showBlacklist = true;
        boolean showServiceclass = true;
        
        if (commandLine.hasOption(LOPT_SHOW_XACML))
            xacmlOutput = true;
        
        if (commandLine.hasOption(LOPT_PLAIN_FORMAT))
            plainFormat = true;
        
        if (commandLine.hasOption(OPT_BLACKLIST))
            showServiceclass = false;
        
        if (commandLine.hasOption(OPT_SERVICECLASS))
            showBlacklist = false;
        
        initOpenSAML();
        
        List<PolicyType> policyList = policyMgmtClient.listPolicies(args[1]);
        
        if (policyList.isEmpty()) {
            System.out.println("No policies has been found.");
            return;
        }
        
        boolean policiesFound;
        
        if (xacmlOutput || plainFormat)
            policiesFound = ListPolicies.listUsingPlaingFormat(policyList, xacmlOutput, true, true, showBlacklist, showServiceclass);
        else
            policiesFound = ListPolicies.listUsingGroupedFormat(policyList, true, true, showBlacklist, showServiceclass, false);
        
        if (!policiesFound)
            System.out.println(ListPolicies.noPoliciesFoundMessage(true, true, showBlacklist, showServiceclass));
        
    }
    
    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_XACML_DESCRIPTION)
                .withLongOpt(LOPT_SHOW_XACML).create());
        
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_PLAIN_FORMAT_DESCRIPTION)
                .withLongOpt(LOPT_PLAIN_FORMAT).create());
        
        return options;
    }
    
}
