package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.policymanagement.PolicyManagementService;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public class PolicyManagementCLI extends ServiceCLI {
    
    protected static final String SERVICE_NAME = "PolicyManagementService";
    protected static PolicyManagementService policyMgmtClient;

    public PolicyManagementCLI(String[] commandNameValues, String usageText, String descriptionText) {
        super(commandNameValues, usageText, descriptionText);
    }

    @Override
    protected Options defineCommandOptions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws ParseException, RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

}
