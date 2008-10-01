package org.glite.authz.pap.client.papmanagement;

import org.glite.authz.pap.client.papmanagement.impl.PAPManagementServiceClientFactoryImpl;

public abstract class PAPManagementServiceClientFactory {
    
    public static PAPManagementServiceClientFactory getPAPManagementServiceClientFactory() {
        return new PAPManagementServiceClientFactoryImpl();
    }

    public abstract PAPManagementServiceClient createPAPManagementServiceClient();

}
