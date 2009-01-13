package org.glite.authz.pap.client;

import org.glite.authz.pap.papmanagement.PAPManagementService;
import org.glite.authz.pap.policymanagement.PolicyManagementService;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;

public interface ServiceClient {
    
    public String getClientCertificate();
    
    public String getClientPrivateKey();
    
    public String getClientPrivateKeyPassword();
    
    public PAPManagementService getPAPManagementService(String url);

    public PolicyManagementService getPolicyManagementService(String url);
    
    public HighLevelPolicyManagement getHighLevelPolicyManagementService(String url);
    
    public PAPAuthorizationManagement getPAPAuthorizationManagementService(String url);
    
    public String getTargetEndpoint();

    public void setClientCertificate(String certFile);
    
    public void setClientPrivateKey(String keyFile);

    public void setClientPrivateKeyPassword(String privateKeyPassword);
    
    public void setTargetEndpoint(String endpointURL);
    
}
