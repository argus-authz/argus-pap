package org.glite.authz.pap.papmanagement.client;

import org.glite.authz.pap.papmanagement.client.impl.PAPManagementServiceClientFactoryImpl;

public abstract class PAPManagementServiceClientFactory {
    
    public static PAPManagementServiceClientFactory getPAPManagementServiceClientFactory() {
        return new PAPManagementServiceClientFactoryImpl();
    }

    public abstract PAPManagementServiceClient createPAPManagementServiceClient();

}
