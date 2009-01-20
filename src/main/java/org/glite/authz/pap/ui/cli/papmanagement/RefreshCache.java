package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;
import org.glite.authz.pap.ui.cli.CLIException;

public class RefreshCache extends PAPManagementCLI {
    
    private static final String USAGE = "[papId] [[papId]...]";
    private static final String[] commandNameValues = { "refresh-cache", "rc" };
    private static final String DESCRIPTION = "Invalidates the local policy cache and retrieves policies " +
    		"from remote PAPs. The arguments identify the PAPs that will be contacted. If no arguments are " +
    		"given, all the trusted PAPs are contacted.";
    
    public RefreshCache() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
        
        String[] args = commandLine.getArgs();
        List<String> papIdList;
        
        if (args.length == 1) {
            papIdList = getAllPAPIds();
        } else {
            papIdList = new LinkedList<String>();
            for (int i = 1; i < args.length; i++) {
                papIdList.add(args[i]);
            }
        }
        
        if (papIdList.isEmpty()) {
            System.out.println("No remote PAPs found.");
            return ExitStatus.SUCCESS.ordinal();
        }
        
        for (String papId : papIdList) {
            System.out.print("Refreshing cache for pap \"" + papId + "\"...");
            // TODO: catch Remote exceptions in order to set exit status information for each PAP
            papMgmtClient.refreshCache(papId);
            System.out.println(" ok.");
        }
        
        return ExitStatus.SUCCESS.ordinal();
        
    }
    
    @Override
    protected Options defineCommandOptions() {
        return null;
    }
    
    private List<String> getAllPAPIds() throws RemoteException {
        List<String> papIdList = new LinkedList<String>();
        
        PAPData[] papDataArray = papMgmtClient.listTrustedPAPs();
        
        for (PAPData papData : papDataArray) {
            papIdList.add(papData.getPapId());
        }
        
        return papIdList;
    }
    
}