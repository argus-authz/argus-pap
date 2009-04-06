package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;

public class RefreshCache extends PAPManagementCLI {

    private static final String[] commandNameValues = { "refresh-cache", "rc" };
    private static final String DESCRIPTION = "Invalidates the local policy cache and retrieves policies "
            + "from remote paps. The arguments identify the paps that will be contacted. If no arguments are "
            + "given, all the remote paps are contacted.";
    private static final String USAGE = "[alias]...]";

    public RefreshCache() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    private List<String> getAllAliases() throws RemoteException {
        List<String> aliasList = new LinkedList<String>();

        PAPData[] papDataArray = papMgmtClient.getAllPAPs();

        for (PAPData papData : papDataArray) {
            if (Pap.PapType.REMOTE.toString().equals(papData.getType())) {
                aliasList.add(papData.getAlias());
            }
        }

        return aliasList;
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws RemoteException {

        String[] args = commandLine.getArgs();
        List<String> aliasList;

        if (args.length == 1) {
            aliasList = getAllAliases();
        } else {
            aliasList = new LinkedList<String>();
            for (int i = 1; i < args.length; i++) {
                aliasList.add(args[i]);
            }
        }

        if (aliasList.isEmpty()) {
            System.out.println("No remote PAPs found.");
            return ExitStatus.SUCCESS.ordinal();
        }

        boolean success = false;
        boolean failure = false;

        for (String alias : aliasList) {
            System.out.print("Refreshing cache for pap \"" + alias + "\"...");

            try {
                papMgmtClient.refreshCache(alias);
                success = true;
            } catch (RemoteException e) {
                System.out.println("error: " + e.getMessage());
                failure = true;
                continue;
            }
            System.out.println(" ok.");
        }

        if (success && failure) {
            return ExitStatus.PARTIAL_SUCCESS.ordinal();
        }

        if (!success && failure) {
            return ExitStatus.FAILURE.ordinal();
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
