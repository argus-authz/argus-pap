package org.glite.authz.pap.papmanagement.client.impl;

import org.glite.authz.pap.papmanagement.client.PAPManagementServiceClient;
import org.glite.authz.pap.papmanagement.client.PAPManagementServicePortType;

public class PAPManagementServiceClientImpl implements PAPManagementServiceClient {
    
    public PAPManagementServicePortType getPAPManagementServicePortType(String url) {
        return new PAPManagementServicePortTypeImpl(url);
    }

}
