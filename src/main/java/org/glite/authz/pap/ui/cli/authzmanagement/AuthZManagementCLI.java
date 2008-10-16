package org.glite.authz.pap.ui.cli.authzmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.papmanagement.PAPManagementService;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class AuthZManagementCLI extends ServiceCLI {
    
    protected static final String SERVICE_NAME = "AuthZManagementService";
    protected PAPManagementService authzMgmtClient;
    
    public AuthZManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }
    
    protected abstract void executeCommand(CommandLine commandLine) throws CLIException,
            ParseException, RemoteException;
    
    @Override
    protected void executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {
        
        // TODO: Cambia getPAPManagementService in getAuthZManagementService (o il nome che gli hai dato)
        authzMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint()
                + SERVICE_NAME);
        
        executeCommand(commandLine);
        
    }
    
}
