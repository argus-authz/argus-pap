package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RemoveAllPolicies extends PolicyManagementCLI {
    
    private static final String USAGE = "[options]";
    private static final String[] commandNameValues = { "remove-all-policies", "rap" };
    private static final String DESCRIPTION = "Delete all policies.";
    
    public RemoveAllPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    protected Options defineCommandOptions() {
        return null;
    }
    
    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        
        XACMLPolicyCLIUtils.initOpenSAML();
        
        PAPPolicyIterator policyIter = new PAPPolicyIterator(xacmlPolicyMgmtClient, null, false);
        
        policyIter.init();
        
        String[] idsArray = policyIter.getPolicyIdsArray();
        
        if (verboseMode)
            System.out.print("Removing " + idsArray.length + " policies..");
 
        int i = 0;
        for (String id : idsArray) {
            
            if (verboseMode) {
                if (i == 10) {
                    i = 0;
                    System.out.print(".");
                } else {
                    i++;
                }
            }
            xacmlPolicyMgmtClient.removePolicyAndReferences(id);
        }
        
        if (verboseMode)
            System.out.println("ok");
        
        return ExitStatus.SUCCESS.ordinal();
    }
    
}
