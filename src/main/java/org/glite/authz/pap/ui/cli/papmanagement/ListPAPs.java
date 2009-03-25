package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;

public class ListPAPs extends PAPManagementCLI {

    private static final String[] commandNameValues = { "list-paps", "lpaps" };
    private static final String DESCRIPTION = "List trusted PAPs.";
    private static final String OPT_LONGLIST_FORMAT = "l";
    private static final String OPT_LONGLIST_FORMAT_DESCRIPTION = "Use a long list format (displays all the information of a PAP).";
    private static final String USAGE = "[options]";

    public ListPAPs() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_LONGLIST_FORMAT_DESCRIPTION)
                                       .create(OPT_LONGLIST_FORMAT));
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        boolean logListFormat = false;

        if (commandLine.hasOption(OPT_LONGLIST_FORMAT))
            logListFormat = true;

        PAPData[] papDataArray = papMgmtClient.getAllPAPs();

        if (papDataArray.length == 0) {
            System.out.println("No remote PAPs has been found.");
            return ExitStatus.SUCCESS.ordinal();
        }

        for (PAPData papData : papDataArray) {
            if (logListFormat)
                System.out.println((new PAP(papData)).toFormattedString());
            else
                System.out.println(String.format("alias = %s (%s)", papData.getAlias(), papData.getType()));
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
