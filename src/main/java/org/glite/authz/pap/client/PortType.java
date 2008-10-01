package org.glite.authz.pap.client;

public interface PortType {
    
    public String getClientCertificate();
    
    public String getClientPrivateKey();

    public String getClientPrivateKeyPassword();
    
    public String getTargetEndpoint();

    public void setClientCertificate(String certFile);
    
    public void setClientPrivateKey(String keyFile);

    public void setClientPrivateKeyPassword(String privateKeyPassword);
    
    public void setTargetEndpoint(String endpointURL);
    
}
