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

package org.glite.authz.pap.client;

import org.bouncycastle.openssl.PasswordFinder;
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
