package org.glite.authz.pap.ui;

import java.rmi.RemoteException;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.client.PAPManagementServiceClientFactory;
import org.glite.authz.pap.papmanagement.client.PAPManagementServicePortType;

public class PAPManagementServiceCLI {
    
    private static final String PAP_MGMT_SERVICE_URL = "https://localhost:8443/pap/services/PAPManagementService";
    private static final PAPManagementServicePortType papMgmtClient;
    
    static {
        PAPManagementServiceClientFactory papMgmtFactory = PAPManagementServiceClientFactory.getPAPManagementServiceClientFactory();
        papMgmtClient = papMgmtFactory.createPAPManagementServiceClient().getPAPManagementServicePortType(PAP_MGMT_SERVICE_URL);
    }
    
    public static void addTrustedPAP(String[] args) throws RemoteException {
        for (String s:args) {
            System.out.println("Inserted: " + s);
        }
        PAP pap = new PAP("alias_prova1", "endpoint_prova1", "dn_prova1");
        papMgmtClient.addTrustedPAP(pap);
    }
    
    public static void ping() throws RemoteException {
        System.out.println(papMgmtClient.ping());
    }
    
    private PAPManagementServiceCLI() { }

}
