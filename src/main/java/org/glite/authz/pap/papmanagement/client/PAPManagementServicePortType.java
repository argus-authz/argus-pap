package org.glite.authz.pap.papmanagement.client;

import org.glite.authz.pap.papmanagement.PAPManagementService;

public interface PAPManagementServicePortType extends PAPManagementService {
    
    public void setClientCertificate(String certFile);

    public void setClientPrivateKey(String keyFile);

    public void setClientPrivateKeyPassword(String privateKeyPassword);

    public void setTargetEndpoint(String endpointURL);

}
