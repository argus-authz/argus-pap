package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class RemovePolicies extends PolicyManagementCLI {
    
    private static final String USAGE = "<policyId> [[policyId] ...] [options]";
    private static final String[] commandNameValues = { "remove-policy", "rp" };
    private static final String DESCRIPTION = "Remove policies.";

    public RemovePolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
        
        String[] args = commandLine.getArgs();
        
        if (args.length < 2)
            throw new ParseException("Missing argument <policyId>");
        
        boolean partialSuccess = false;
        boolean failure = false;
        
        try {
        	
	        for (int i=1; i<args.length; i++) {
	            
	            String policyId = args[i];
	            System.out.print("Removing \"" + policyId + "\"... ");
	            
	            boolean policyRemoved = xacmlPolicyMgmtClient.removeObjectByIdAndReferences(policyId);
	            
	            if (!policyRemoved) {
	            	System.out.println("NOT FOUND.");
	            	failure = true;
	            	continue;
	            }
	            
	            partialSuccess = true;
	            
	            System.out.println("ok.");
	        }
        } catch (RemoteException e) {
        	System.out.println("ERROR.");
        	throw e;
        }
        
        if (failure && !partialSuccess)
            return ExitStatus.FAILURE.ordinal();
        
        if (failure && partialSuccess)
            return ExitStatus.PARTIAL_SUCCESS.ordinal();
        
        return ExitStatus.SUCCESS.ordinal();
        
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

}
