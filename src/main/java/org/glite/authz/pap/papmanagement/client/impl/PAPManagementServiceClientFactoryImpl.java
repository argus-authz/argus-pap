package org.glite.authz.pap.papmanagement.client.impl;

import org.glite.authz.pap.papmanagement.client.PAPManagementServiceClient;
import org.glite.authz.pap.papmanagement.client.PAPManagementServiceClientFactory;

public class PAPManagementServiceClientFactoryImpl extends PAPManagementServiceClientFactory {

    @Override
    public PAPManagementServiceClient createPAPManagementServiceClient() {
        return new PAPManagementServiceClientImpl();
    }

}
