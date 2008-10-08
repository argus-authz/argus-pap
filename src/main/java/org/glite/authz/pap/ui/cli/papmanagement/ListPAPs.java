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
    
    @Override
    protected boolean executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        
        List<PAP> papList = papMgmtClient.listTrustedPAPs();
        
        for (PAP pap:papList) {
            System.out.println(pap.toString());
        }
        return true;
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected String[] defineCommandNameValues() {
        return commandNameValues;
    }

    @Override
    protected String defineDescription() {
        return DESCRIPTION;
    }

    @Override
    protected String defineLongDescription() {
        return null;
    }

    @Override
    protected String defineUsage() {
        return USAGE;
    }

}
