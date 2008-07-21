package org.glite.authz.pap.distribution;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.glite.authz.pap.provisioning.client.ProvisioningServiceClient;
import org.glite.authz.pap.provisioning.client.ProvisioningServiceClientFactory;
import org.glite.authz.pap.provisioning.client.ProvisioningServicePortType;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Statement;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPClient {

    private static final ProvisioningServiceClient client;
    private static final XACMLPolicyQueryType xacmlPolicyQuery;
    static {
        ProvisioningServiceClientFactory factory = ProvisioningServiceClientFactory
                .getProvisioningServiceClientFactory();
        client = factory.createPolicyProvisioningServiceClient();
        xacmlPolicyQuery = makeStandardPAPQuery();
    }

    private static XACMLPolicyQueryType makeStandardPAPQuery() {

        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

        XACMLPolicyQueryType xacmlPolicyQuery = (XACMLPolicyQueryType) builderFactory.getBuilder(
                XACMLPolicyQueryType.TYPE_NAME_XACML20).buildObject(
                XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getNamespaceURI(),
                XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getLocalPart(),
                XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getPrefix());

        IssuerBuilder issuerBuilder = (IssuerBuilder) builderFactory
                .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);

        Issuer issuer = issuerBuilder.buildObject();

        issuer.setValue("ISSUER_STRING");
        issuer.setFormat(NameID.ENTITY);

        xacmlPolicyQuery.setIssuer(issuer);
        xacmlPolicyQuery.setIssueInstant(new DateTime());

        xacmlPolicyQuery.setVersion(SAMLVersion.VERSION_20);
        xacmlPolicyQuery.setID("_12345");
        xacmlPolicyQuery.setIssueInstant(new DateTime());

        // Set: this is a PAP query
        ExtensionsBuilder eBuilder = (ExtensionsBuilder) builderFactory.getBuilder(new QName(
                SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX));
        Extensions extensions = eBuilder.buildObject();

        xacmlPolicyQuery.setExtensions(extensions);

        return xacmlPolicyQuery;
    }

    private final Logger log = LoggerFactory.getLogger(PAPClient.class);

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
            // TODO: do something better
            e.printStackTrace();
            resultList = new LinkedList<XACMLObject>();
        } catch (ServiceException e) {
            // TODO: do something better
            e.printStackTrace();
            resultList = new LinkedList<XACMLObject>();
        }
        return resultList;
    }

    private List<XACMLObject> getXACMLObjectList(Response response) {

        List<XACMLObject> responseList = new LinkedList<XACMLObject>();

        for (Assertion assertion : response.getAssertions()) {
            for (Statement statement : assertion.getStatements()) {
                String statementLocalName = statement.getSchemaType().getLocalPart();

                if ("XACMLPolicyStatementType".equals(statementLocalName)) {

                    XACMLPolicyStatementType policyStatement = (XACMLPolicyStatementType) statement;
                    responseList.addAll(policyStatement.getPolicySets());
                    responseList.addAll(policyStatement.getPolicies());
                }
            }
        }
        return responseList;
    }
}
