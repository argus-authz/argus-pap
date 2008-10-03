package org.glite.authz.pap.ui.cli.papmanagement;

import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.papmanagement.PAPManagementService;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class PAPManagementCLI extends ServiceCLI {
    
    protected static final String SERVICE_NAME = "PAPManagementService";
    protected static PAPManagementService papMgmtClient;
    
    public boolean execute(String[] args, ServiceClient serviceClient) {
        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint() + SERVICE_NAME);
        return executeCommand(args);
    }
    
    protected abstract boolean executeCommand(String[] args);
    
}
