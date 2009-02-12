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
    
    public PAPManagement getPAPManagementService(String url);

    public XACMLPolicyManagement getXACMLPolicyManagementService(String url);
    
    public HighLevelPolicyManagement getHighLevelPolicyManagementService(String url);
    
    public PAPAuthorizationManagement getPAPAuthorizationManagementService(String url);
    
    public Provisioning getProvisioningService(String url);
    
    public String getTargetEndpoint();

    public void setClientCertificate(String certFile);
    
    public void setClientPrivateKey(String keyFile);

    public void setClientPrivateKeyPassword(String privateKeyPassword);
    
    public void setTargetEndpoint(String endpointURL);
    
    public void setClientProxy(String clientProxy);
    
}
