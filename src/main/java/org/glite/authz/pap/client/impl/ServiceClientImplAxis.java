package org.glite.authz.pap.client.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Service;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagement;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagementServiceLocator;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagementServiceLocator;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagementServiceLocator;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
import org.glite.authz.pap.services.provisioning.axis_skeletons.ProvisioningServiceLocator;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagementServiceLocator;
import org.glite.security.trustmanager.axis.AXISSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceClientImplAxis implements ServiceClient {

    public final Logger log = LoggerFactory.getLogger(ServiceClientImplAxis.class);

    private static final String DEFAULT_SSL_CERT_FILE = "/etc/grid-security/hostcert.pem";
    private static final String DEFAULT_SSL_KEY = "/etc/grid-security/hostkey.pem";
    private String serviceURL = null;
    private String clientCertificate = null;
    private String clientPrivateKey = null;
    private String clientPrivateKeyPassword = null;

    public ServiceClientImplAxis() {}

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

    public HighLevelPolicyManagement getHighLevelPolicyManagementService(String url) {

        initializeAxisProperties();
        HighLevelPolicyManagementServiceLocator loc = new HighLevelPolicyManagementServiceLocator();

        try {
            return loc.getHighLevelPolicyManagementService( new URL(url) );

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting Highlevel Policy management service: " + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting HighLevel Policy management service: " + e.getMessage(), e);

        }
    }

    public PAPAuthorizationManagement getPAPAuthorizationManagementService(String url) {

        initializeAxisProperties();
        PAPAuthorizationManagementServiceLocator loc = new PAPAuthorizationManagementServiceLocator();

        try {
            return loc.getPAPAuthorizationManagement(new URL(url));

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting PAP Authorization management service: " + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting PAP Authorization management service: " + e.getMessage(), e);

        }
    }

    public PAPManagement getPAPManagementService(String url) {

        initializeAxisProperties();
        PAPManagementServiceLocator loc = new PAPManagementServiceLocator();

        try {
            return loc.getPAPManagementService( new URL(url) );

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting PAP Management service: " + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting PAP Management service: " + e.getMessage(), e);

        }
    }

    public Service getService() {
        initializeAxisProperties();
        return new Service();
    }

    public String getTargetEndpoint() {
        return serviceURL;
    }

    public XACMLPolicyManagement getXACMLPolicyManagementService(String url) {

        initializeAxisProperties();
        XACMLPolicyManagementServiceLocator loc = new XACMLPolicyManagementServiceLocator();
        TypeMapping typeMapping = loc.getTypeMappingRegistry().getDefaultTypeMapping();
        
        typeMapping.register(org.opensaml.xacml.policy.PolicyType.class,
                org.opensaml.xacml.policy.PolicyType.SCHEMA_TYPE_NAME,
                new org.glite.authz.pap.provisioning.axis.SerializerFactory(),
                new org.glite.authz.pap.provisioning.axis.DeserializerFactory());
        
        typeMapping.register(org.opensaml.xacml.policy.PolicySetType.class,
                org.opensaml.xacml.policy.PolicySetType.SCHEMA_TYPE_NAME,
                new org.glite.authz.pap.provisioning.axis.SerializerFactory(),
                new org.glite.authz.pap.provisioning.axis.DeserializerFactory());

        try {
            return loc.getXACMLPolicyManagementService( new URL(url) );

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting XACML Policy management service: " + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting XACML Policy management service: " + e.getMessage(), e);

        }
    }
    
    public Provisioning getProvisioningService(String url) {

        initializeAxisProperties();
        ProvisioningServiceLocator loc = new ProvisioningServiceLocator();
        TypeMapping typeMapping = loc.getTypeMappingRegistry().getDefaultTypeMapping();
        
        typeMapping.register(org.opensaml.xacml.profile.saml.XACMLPolicyQueryType.class,
                org.opensaml.xacml.profile.saml.XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20,
                new org.glite.authz.pap.provisioning.axis.SerializerFactory(),
                new org.glite.authz.pap.provisioning.axis.DeserializerFactory());

        typeMapping.register(org.opensaml.saml2.core.Response.class,
                org.opensaml.saml2.core.Response.DEFAULT_ELEMENT_NAME,
                new org.glite.authz.pap.provisioning.axis.SerializerFactory(),
                new org.glite.authz.pap.provisioning.axis.DeserializerFactory());

        try {
            return loc.getProvisioningService( new URL(url) );

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting Provisioning Policy management service: " + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting Provisioning Policy management service: " + e.getMessage(), e);

        }
    }

    public void setClientCertificate(String certFile) {
        log.debug("clientCertificate=" + certFile);
        clientCertificate = certFile;
    }

    public void setClientPrivateKey(String keyFile) {
        log.debug("clientPrivateKey=" + keyFile);
        clientPrivateKey = keyFile;
    }

    public void setClientPrivateKeyPassword(String privateKeyPassword) {
        clientPrivateKeyPassword = privateKeyPassword;
    }

    public void setTargetEndpoint(String endpointURL) {
        serviceURL = endpointURL;
    }

    protected void initializeAxisProperties() {

        AxisProperties.setProperty("axis.socketSecureFactory", "org.glite.security.trustmanager.axis.AXISSocketFactory");

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
        System.setProperties(properties);

    }

}
