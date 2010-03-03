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

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;

public class ProvisioningServiceStub implements Provisioning {
    
    private Call call;
    
    public ProvisioningServiceStub(java.net.URL url, javax.xml.rpc.Service service) throws ServiceException {
        
        call = (Call) service.createCall();

        call.setOperationName(new QName("http://change.me.please.i.am.fictious", "xacmlPolicyQuery"));
        call.setOperationStyle(Style.DOCUMENT);
        call.setOperationUse(Use.LITERAL);
        call.setTargetEndpointAddress(url);
        
    }
    
    public Response XACMLPolicyQuery(XACMLPolicyQueryType query) throws RemoteException {
        
        Response response = (Response) call.invoke(new Object[] { query });

        return response;
    }

}
