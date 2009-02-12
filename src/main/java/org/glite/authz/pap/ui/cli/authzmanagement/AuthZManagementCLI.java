package org.glite.authz.pap.ui.cli.authzmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagement;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class AuthZManagementCLI extends ServiceCLI {
    
    protected PAPAuthorizationManagement authzMgmtClient;
    
    public AuthZManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }
    
    protected abstract int executeCommand(CommandLine commandLine) throws CLIException,
            ParseException, RemoteException;
    
    @Override
    protected int executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {
        
        authzMgmtClient = serviceClient.getPAPAuthorizationManagementService( serviceClient.getTargetEndpoint()
                + serviceClient.getPAPAuthorizationManagementServiceName() );
        
        return executeCommand(commandLine);
        
    }
    
}
