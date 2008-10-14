package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.opensaml.xacml.policy.PolicyType;

public class RemovePolicy extends PolicyManagementCLI {
    
    private static final String USAGE = "<policyId> [[policyId] ...]";
    private static final String[] commandNameValues = { "remove-policy", "rp" };
    private static final String DESCRIPTION = "Remove policies.";

    public RemovePolicy() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected void executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {
        
        String[] args = commandLine.getArgs();
        
        if (args.length < 2)
            throw new ParseException("Missing argument <policyId>");
        
        initOpenSAML();
        
        try {
        	
	        for (int i=1; i<args.length; i++) {
	            String policyId = args[i];
	            System.out.print("Removing policy \"" + policyId + "\"... ");
	            
	            if (!policyMgmtClient.hasPolicy(policyId)) {
	            	System.out.println("NOT FOUND.");
	            	continue;
	            }
	            
	            PolicyType policy = policyMgmtClient.getPolicy(policyId);
	            
	            PolicyWizard policyWizard = new PolicyWizard(policy);
	            
	            removePolicy(policyWizard);
	            
	            System.out.println("ok.");
	        }
        } catch (RemoteException e) {
        	System.out.println("ERROR.");
        	e.printStackTrace();
        	throw e;
        }
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

}
