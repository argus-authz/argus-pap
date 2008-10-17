package org.glite.authz.pap.client.impl;

import java.util.Properties;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Service;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.papmanagement.PAPManagementService;
import org.glite.authz.pap.policymanagement.PolicyManagementService;
import org.glite.security.trustmanager.axis.AXISSocketFactory;

public class ServiceClientImplAxis implements ServiceClient {
    
    private static final String DEFAULT_SSL_CERT_FILE = "/etc/grid-security/hostcert.pem";
    private static final String DEFAULT_SSL_KEY = "/etc/grid-security/hostkey.pem";
    private String serviceURL = null;
    private String clientCertificate = null;
    private String clientPrivateKey = null;
    private String clientPrivateKeyPassword = null;
    
    public ServiceClientImplAxis() { }
    
    public ServiceClientImplAxis(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public String getClientCertificate() {
        return clientCertificate;
    }

    public String getClientPrivateKey() {
        return clientPrivateKey;
    }

    public String getClientPrivateKeyPassword() {
        return clientPrivateKeyPassword;
    }

    public PAPManagementService getPAPManagementService(String url) {
        return new PAPManagementServiceClientImpl(url, getService());
    }

    public PolicyManagementService getPolicyManagementService(String url) {
        return new PolicyManagementServiceClientImpl(url, getService());
    }

    public Service getService() {
        AxisProperties.setProperty("axis.socketSecureFactory",
        "org.glite.security.trustmanager.axis.AXISSocketFactory");
    	
        System.setProperty("crlUpdateInterval", "0s");

        // need to pass property to AXISSocketFactory
        Properties properties = AXISSocketFactory.getCurrentProperties();
        
        // TODO will get cert and key from the configuration, with those as
        // default
        
        if (clientCertificate == null)
            properties.setProperty("sslCertFile", DEFAULT_SSL_CERT_FILE);
        else
            properties.setProperty("sslCertFile", clientCertificate);
        
        if (clientPrivateKey == null)
            properties.setProperty("sslKey", DEFAULT_SSL_KEY);
        else
            properties.setProperty("sslKey", clientPrivateKey);
        
        if (clientPrivateKeyPassword != null)
            properties.setProperty("sslKeyPasswd", clientPrivateKeyPassword);
        
        AXISSocketFactory.setCurrentProperties(properties);
        
        return new Service();
    }

    public String getTargetEndpoint() {
        return serviceURL;
    }
    
    public void setClientCertificate(String certFile) {
        clientCertificate = certFile;
    }

    public void setClientPrivateKey(String keyFile) {
        clientPrivateKey = keyFile;
    }

    public void setClientPrivateKeyPassword(String privateKeyPassword) {
        clientPrivateKeyPassword = privateKeyPassword;
    }

    public void setTargetEndpoint(String endpointURL) {
        serviceURL = endpointURL;
    }

}
