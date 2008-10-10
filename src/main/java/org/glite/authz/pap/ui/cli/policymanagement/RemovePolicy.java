package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.glite.authz.pap.ui.cli.CLIException;

public class RemovePolicy extends PolicyManagementCLI {
    
    private static final String USAGE = "<file> [[file] ...]";
    private static final String[] commandNameValues = { "remove-policy", "rp" };
    private static final String DESCRIPTION = "Add policies defined by <file>";

    public RemovePolicy() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected boolean executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
        
        String[] args = commandLine.getArgs();
        
        if (args.length < 2)
            throw new ParseException("Missing policyId");
        
        for (int i=1; i<args.length; i++) {
            String policyId = args[i];
            System.out.print("Removing policy '" + policyId + "'... ");
            
            try {
                //policyMgmtClient.removePolicy(policyId);
                System.out.print(policyMgmtClient.hasPolicy(policyId));
                System.out.println("... success.");
            } catch (NotFoundException e) {
                System.out.println("NOT FOUND!");
            } catch (RepositoryException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    protected Options defineCommandOptions() {
        // TODO Auto-generated method stub
        return null;
    }

}
