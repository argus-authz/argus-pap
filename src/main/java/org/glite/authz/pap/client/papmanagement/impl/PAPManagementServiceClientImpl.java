package org.glite.authz.pap.client.papmanagement.impl;

import org.glite.authz.pap.client.papmanagement.PAPManagementServiceClient;
import org.glite.authz.pap.client.papmanagement.PAPManagementServicePortType;

public class PAPManagementServiceClientImpl implements PAPManagementServiceClient {
    
    public PAPManagementServicePortType getPAPManagementServicePortType(String url) {
        return new PAPManagementServicePortTypeImpl(url);
    }

}
