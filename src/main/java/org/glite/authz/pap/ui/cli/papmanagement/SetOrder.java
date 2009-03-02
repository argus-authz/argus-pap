package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class SetOrder extends PAPManagementCLI {
    
    private static final String USAGE = "[alias_01] [[alias_02]...]";
    private static final String[] commandNameValues = { "set-paps-order", "spo" };
    private static final String DESCRIPTION = "Define the order of the remote PAPs.";
    
    public SetOrder() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
        
        String[] args = commandLine.getArgs();
        
        String[] aliasArray = null;

        int nAlias = args.length - 1;
        
        if (nAlias > 0) {
        	aliasArray = new String[nAlias];
            for (int i = 0; i < nAlias; i++) {
            	aliasArray[i] = args[i + 1];
            }
        } else {
        	System.out.println("Clearing current order definition");
        }
        
        papMgmtClient.setOrder(aliasArray);
        
        return ExitStatus.SUCCESS.ordinal();
        
    }
    
    @Override
    protected Options defineCommandOptions() {
        return null;
    }
}
