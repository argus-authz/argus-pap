package org.glite.authz.pap.distribution;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.glite.authz.pap.provisioning.client.ProvisioningServiceClient;
import org.glite.authz.pap.provisioning.client.ProvisioningServiceClientFactory;
import org.glite.authz.pap.provisioning.client.ProvisioningServicePortType;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPClient {

    private final Logger log = LoggerFactory.getLogger(PAPClient.class);
    private static final ProvisioningServiceClient client;
    private static final XACMLPolicyQueryType xacmlPolicyQuery;
    static {
        ProvisioningServiceClientFactory factory = ProvisioningServiceClientFactory
                .getProvisioningServiceClientFactory();
        client = factory.createPolicyProvisioningServiceClient();
        xacmlPolicyQuery = makeStandardPAPQuery();
    }

    private static XACMLPolicyQueryType makeStandardPAPQuery() {
        XACMLPolicyQueryType xacmlPolicyQuery = (XACMLPolicyQueryType) Configuration.getBuilderFactory()
                .getBuilder(XACMLPolicyQueryType.TYPE_NAME_XACML20).buildObject(
                        XACMLPolicyQueryType.TYPE_NAME_XACML20);
        RequestType request = (RequestType) Configuration.getBuilderFactory().getBuilder(
                RequestType.DEFAULT_ELEMENT_NAME).buildObject(RequestType.DEFAULT_ELEMENT_NAME);
        xacmlPolicyQuery.getRequests().add(request);
        return xacmlPolicyQuery;
    }

    private final ProvisioningServicePortType port;

    public PAPClient(String url) {
        port = client.getProvisioningServicePortType(url);
    }

    public List<XACMLObject> getLocalPolicies() {
        List<XACMLObject> resultList = null;
        Response response;
        try {
            response = port.xacmlPolicyQuery(xacmlPolicyQuery);
            resultList = getXACMLObjectList(response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            log.error("Remote exception");
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            log.error("Service exception");
        }
        return resultList;
    }

    private List<XACMLObject> getXACMLObjectList(Response response) {
        List<XACMLObject> responseList = new LinkedList<XACMLObject>();
        Extensions extensions =  response.getExtensions();
        List<XMLObject> xacmlPolicyStatementList = extensions.getUnknownXMLObjects(XACMLPolicyStatementType.DEFAULT_ELEMENT_NAME);
        for (XMLObject xmlObject:xacmlPolicyStatementList) {
            XACMLPolicyStatementType xacmlPolicyStatement = (XACMLPolicyStatementType) xmlObject;
            responseList.addAll(xacmlPolicyStatement.getPolicySets());
            responseList.addAll(xacmlPolicyStatement.getPolicies());
        }
        return responseList;
    }
}
