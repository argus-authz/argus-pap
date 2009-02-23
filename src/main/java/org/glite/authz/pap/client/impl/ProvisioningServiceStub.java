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
