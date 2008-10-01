package org.glite.authz.pap.client.papmanagement.impl;

import org.glite.authz.pap.client.papmanagement.PAPManagementServiceClient;
import org.glite.authz.pap.client.papmanagement.PAPManagementServiceClientFactory;

public class PAPManagementServiceClientFactoryImpl extends PAPManagementServiceClientFactory {

    @Override
    public PAPManagementServiceClient createPAPManagementServiceClient() {
        return new PAPManagementServiceClientImpl();
    }

}
