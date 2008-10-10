package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RemovePAP extends PAPManagementCLI {
    
    private static final String USAGE = "<pap-id>";
    private static final String[] commandNameValues = { "remove-pap", "rpap" };
    private static final String DESCRIPTION = "Remove a trusted PAP and delete the cached policies.";
    
    public RemovePAP() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected boolean executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if (args.length != 2)
            throw new ParseException("Wrong number of arguments");
        
        String papId = args[1];
        
        papMgmtClient.removeTrustedPAP(papId);
        
        return true;
    }

}
