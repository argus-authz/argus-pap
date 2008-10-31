package org.glite.authz.pap.ui.cli.authzmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagement;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class AuthZManagementCLI extends ServiceCLI {
    
    protected static final String SERVICE_NAME = "AuthorizationManagementService";
    protected PAPAuthorizationManagement authzMgmtClient;
    
    public AuthZManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }
    
    protected abstract void executeCommand(CommandLine commandLine) throws CLIException,
            ParseException, RemoteException;
    
    @Override
    protected void executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {
        
        authzMgmtClient = serviceClient.getPAPAuthorizationManagementService( serviceClient.getTargetEndpoint()
                + SERVICE_NAME );
        
        executeCommand(commandLine);
        
    }
    
}
