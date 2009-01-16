package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;

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
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        
        PAPData[] papDataArray = papMgmtClient.listTrustedPAPs();
        
        if (papDataArray.length == 0) {
        	System.out.println("No remote PAPs has been found.");
        	return ExitStatus.SUCCESS.ordinal();
        }
        	
        for (PAPData papData:papDataArray) {
            System.out.println((new PAP(papData)).toFormattedString());
        }
        
        return ExitStatus.SUCCESS.ordinal();
        
    }
}
