package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.Pap;

public class ListPaps extends PAPManagementCLI {

    private static final String[] commandNameValues = { "list-paps", "lpaps" };
    private static final String DESCRIPTION = "List all defined paps.";
    private static final String OPT_LONGLIST_FORMAT = "l";
    private static final String OPT_LONGLIST_FORMAT_DESCRIPTION = "Use a long list format (displays all the information of a pap).";
    private static final String USAGE = "[options]";

    public ListPaps() {
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

        Pap[] papArray = papMgmtClient.getAllPaps();

        if (papArray.length == 0) {
            System.out.println("No remote PAPs has been found.");
            return ExitStatus.SUCCESS.ordinal();
        }

        for (Pap pap : papArray) {
            if (logListFormat)
                System.out.println(pap.toFormattedString());
            else {
                String visibility;
                
                if (pap.isVisibilityPublic()) {
                    visibility = "public";
                } else {
                    visibility = "private";
                }
                
                String enabledString;
                
                if (pap.isEnabled()) {
                    enabledString = "enabled";
                } else {
                    enabledString = "disabled";
                }
                
                if (pap.isLocal()) {
                    System.out.println(String.format("alias = %s (%s, %s, %s)", pap.getAlias(), pap.getTypeAsString(), enabledString, visibility));
                } else {
                    System.out.println(String.format("alias = %s (%s, %s, %s, %s)", pap.getAlias(), pap.getTypeAsString(), enabledString, visibility, pap.getEndpoint()));
                }
            }
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
