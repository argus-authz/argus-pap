package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.PAP;

public class ListPAPs extends PAPManagementCLI {
    
    private static final String USAGE = "";
    private static final String[] commandNameValues = { "list-paps", "lpaps" };
    private static final String DESCRIPTION = "List trusted PAPs.";
    
    public ListPAPs() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected void executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        
        List<PAP> papList = papMgmtClient.listTrustedPAPs();
        
        if (papList.isEmpty()) {
        	System.out.println("No remote PAPs found.");
        	return;
        }
        	
        for (PAP pap:papList) {
            System.out.println(pap.toFormattedString());
        }
        
    }
}
