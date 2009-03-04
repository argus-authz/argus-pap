package org.glite.authz.pap.client;

import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;

public interface ServiceClient {
    
    public String getClientCertificate();
    
    public String getClientPrivateKey();
    
    public String getClientPrivateKeyPassword();
    
    public String getClientProxy();
    
    public HighLevelPolicyManagement getHighLevelPolicyManagementService(String url);

    public String getHighLevelPolicyManagementServiceName();
    
    public PAPAuthorizationManagement getPAPAuthorizationManagementService(String url);
    
    public String getPAPAuthorizationManagementServiceName();
    
    public PAPManagement getPAPManagementService(String url);
    
    public String getPAPManagementServiceName();
    
    public Provisioning getProvisioningService(String url);
    
    public String getProvisioningServiceName();
    
    public String getTargetEndpoint();
    
    public XACMLPolicyManagement getXACMLPolicyManagementService(String url);
    
    public String getXACMLPolicyManagementServiceName();

    public void setClientCertificate(String certFile);
    
    public void setClientPrivateKey(String keyFile);

    public void setClientPrivateKeyPassword(String privateKeyPassword);
    
    public void setClientProxy(String clientProxy);
    
    public void setTargetEndpoint(String endpointURL);
    
}
