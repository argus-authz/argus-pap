/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.client.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.AxisProperties;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.impl.axis.CANLAxis1SocketFactory;
import org.glite.authz.pap.client.impl.axis.DefaultConfigurator;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagement;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagementServiceLocator;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagementServiceLocator;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagementServiceLocator;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagementServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceClientImplAxis implements ServiceClient {
	
	public static final String AXIS_SOCKET_FACTORY_PROPERTY = "axis.socketSecureFactory";
    
    public final Logger log = LoggerFactory.getLogger(ServiceClientImplAxis.class);
    private String clientCertificate = null;
    private String clientPrivateKey = null;
    private String clientPrivateKeyPassword = null;
    private String clientProxy = null;
    private String serviceURL = null;

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

    public String getClientProxy() {

        return clientProxy;
    }

    public HighLevelPolicyManagement getHighLevelPolicyManagementService(String url) {

        initializeAxisProperties();
        HighLevelPolicyManagementServiceLocator loc = new HighLevelPolicyManagementServiceLocator();

        try {
            return loc.getHighLevelPolicyManagementService(new URL(url));

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting Highlevel Policy management service: " + e.getMessage(),
                                   e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting HighLevel Policy management service: " + e.getMessage(),
                                   e);

        }
    }

    public String getHighLevelPolicyManagementServiceName() {
        return "HighLevelPolicyManagementService";
    }

    public PAPAuthorizationManagement getPAPAuthorizationManagementService(String url) {

        initializeAxisProperties();
        PAPAuthorizationManagementServiceLocator loc = new PAPAuthorizationManagementServiceLocator();

        try {
            return loc.getPAPAuthorizationManagement(new URL(url));

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting PAP Authorization management service: " + e.getMessage(),
                                   e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting PAP Authorization management service: " + e.getMessage(),
                                   e);

        }
    }

    public String getPAPAuthorizationManagementServiceName() {
        return "AuthorizationManagementService";
    }

    public PAPManagement getPAPManagementService(String url) {

        initializeAxisProperties();
        PAPManagementServiceLocator loc = new PAPManagementServiceLocator();

        try {
            return loc.getPAPManagementService(new URL(url));

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting PAP Management service: " + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting PAP Management service: " + e.getMessage(), e);

        }
    }

    public String getPAPManagementServiceName() {
        return "PAPManagementService";
    }

    public Provisioning getProvisioningService(String url) {

        initializeAxisProperties();

        ProvisioningServiceLocator loc = new ProvisioningServiceLocator();

        TypeMapping typeMapping = loc.getTypeMappingRegistry().getDefaultTypeMapping();

        typeMapping.register(org.opensaml.xacml.profile.saml.XACMLPolicyQueryType.class,
                             org.opensaml.xacml.profile.saml.XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20,
                             new org.glite.authz.pap.common.opensamlserializer.SerializerFactory(),
                             new org.glite.authz.pap.common.opensamlserializer.DeserializerFactory());

        typeMapping.register(org.opensaml.saml2.core.Response.class,
                             org.opensaml.saml2.core.Response.DEFAULT_ELEMENT_NAME,
                             new org.glite.authz.pap.common.opensamlserializer.SerializerFactory(),
                             new org.glite.authz.pap.common.opensamlserializer.DeserializerFactory());

        try {
            return loc.getProvisioningService(new URL(url));

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting Provisioning Policy management service: "
                    + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting Provisioning Policy management service: "
                    + e.getMessage(), e);

        }
    }

    public String getProvisioningServiceName() {
        return "ProvisioningService";
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
                             new org.glite.authz.pap.common.opensamlserializer.SerializerFactory(),
                             new org.glite.authz.pap.common.opensamlserializer.PolicyTypeDeserializerFactory());

        typeMapping.register(org.opensaml.xacml.policy.PolicySetType.class,
                             org.opensaml.xacml.policy.PolicySetType.SCHEMA_TYPE_NAME,
                             new org.glite.authz.pap.common.opensamlserializer.SerializerFactory(),
                             new org.glite.authz.pap.common.opensamlserializer.PolicySetTypeDeserializerFactory());

        try {
            return loc.getXACMLPolicyManagementService(new URL(url));

        } catch (MalformedURLException e) {
            throw new PAPException("Error contacting XACML Policy management service: " + e.getMessage(), e);

        } catch (ServiceException e) {
            throw new PAPException("Error contacting XACML Policy management service: " + e.getMessage(), e);

        }
    }

    public String getXACMLPolicyManagementServiceName() {
        return "XACMLPolicyManagementService";
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

    public void setClientProxy(String clientProxy) {

        this.clientProxy = clientProxy;

    }

    public void setTargetEndpoint(String endpointURL) {
        serviceURL = endpointURL;
    }

    protected void initializeAxisProperties() {

        DefaultConfigurator socketFactoryConfigurator = new DefaultConfigurator();

        if (clientProxy != null) {
            
        	socketFactoryConfigurator.setProxyFile(clientProxy);
            
        } else {

        	if (clientCertificate != null)
        		socketFactoryConfigurator.setCertFile(clientCertificate);

            if (clientPrivateKey != null)
            	socketFactoryConfigurator.setKeyFile(clientPrivateKey);
           
            if (clientPrivateKeyPassword != null)
            	socketFactoryConfigurator.setKeyPassword(clientPrivateKeyPassword);
        }

        CANLAxis1SocketFactory.setConfigurator(socketFactoryConfigurator);
        String socketFactoryClass = CANLAxis1SocketFactory.class.getName();
        AxisProperties.setProperty(AXIS_SOCKET_FACTORY_PROPERTY, socketFactoryClass);
        // System.setProperty(AXIS_SOCKET_FACTORY_PROPERTY, socketFactoryClass);
    }
}
