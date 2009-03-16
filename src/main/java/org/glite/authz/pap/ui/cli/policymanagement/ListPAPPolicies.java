package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;
import org.glite.authz.pap.ui.cli.CLIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListPAPPolicies extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "list-pap-policies", "lpp" };
    private static final String DESCRIPTION = "List cached policies of the trusted PAPs speficied as argument. "
            + "If no \"pap_alias\" are specified then cached policies for all the remote trusted PAPs are listed";
    private static final Logger log = LoggerFactory.getLogger(ListPAPPolicies.class);
    private static final String USAGE = "[pap_alias] [options]";

    private String[] papAliasArray;
    private String[] papInfoArray;
    private boolean showIds = false;
    private boolean showRulesId = false;
    private boolean xacmlOutput = false;

    public ListPAPPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    private int listPAPPolicies(String papAlias, String papInfo) {

        System.out.println(papInfo);

        boolean policiesFound = false;

//        policiesFound = ListPolicies.listPolicies(papAlias, showIds, showRulesId, xacmlOutput);

        if (!policiesFound) {
            printOutputMessage("No policies has benn found.");
        }

        System.out.println();

        return ExitStatus.SUCCESS.ordinal();

    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();

        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_XACML_DESCRIPTION)
                .withLongOpt(OPT_SHOW_XACML_LONG).create());

        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length == 1) {
            PAPData[] papDataArray = papMgmtClient.listTrustedPAPs();
            int size = papDataArray.length;
            papAliasArray = new String[size];
            papInfoArray = new String[size];
            for (int i = 0; i < size; i++) {
                papAliasArray[i] = papDataArray[i].getAlias();
                papInfoArray[i] = String.format("%s (%s:%s):", papDataArray[i].getAlias(), papDataArray[i].getHostname(),
                        papDataArray[i].getPort());
            }
        } else {
            int size = args.length - 1;
            papAliasArray = new String[size];
            papInfoArray = new String[size];
            for (int i = 0; i < size; i++) {
                papAliasArray[i] = args[i + 1];
                papInfoArray[i] = String.format("%s: ", papAliasArray[i]);
            }
        }

        if (papAliasArray.length == 0) {
            System.out.println("No remote PAPs has been found.");
            return ExitStatus.SUCCESS.ordinal();
        }
        
        if (commandLine.hasOption(OPT_SHOW_XACML_LONG)) {
            xacmlOutput = true;
        }

        if (commandLine.hasOption(OPT_SHOW_IDS)) {
            showIds = true;
        }
        
        if (commandLine.hasOption(OPT_SHOW_ALL_IDS_LONG)) {
            showRulesId = true;
        }

        XACMLPolicyCLIUtils.initOpenSAML();

        boolean failure = false;
        boolean partialSuccess = false;

        for (int i = 0; i < papAliasArray.length; i++) {

            if (ExitStatus.SUCCESS.ordinal() == listPAPPolicies(papAliasArray[i], papInfoArray[i])) {
                failure = false;
                partialSuccess = true;
            } else {
                failure = true;
            }
        }

        if (failure && !partialSuccess) {
            return ExitStatus.FAILURE.ordinal();
        }

        if (failure && partialSuccess) {
            return ExitStatus.PARTIAL_SUCCESS.ordinal();
        }

        return ExitStatus.SUCCESS.ordinal();
    }

}
