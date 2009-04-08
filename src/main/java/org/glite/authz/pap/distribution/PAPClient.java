package org.glite.authz.pap.distribution;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.ServiceClientFactory;
import org.glite.authz.pap.common.xacml.impl.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.impl.PolicyTypeString;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
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
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A client to fetch policies from another PAP. */
public class PAPClient {

    private static final Logger log = LoggerFactory.getLogger(PAPClient.class);
    private final Provisioning provisioningClient;
    private final String url;

    private XACMLPolicyQueryType xacmlPolicyQuery;

    /**
     * Contructor.
     * 
     * @param url the URL, as <code>String</code> of the PAP to contact.
     */
    public PAPClient(String url) {

        ServiceClientFactory serviceClientFactory = ServiceClientFactory.getServiceClientFactory();
        ServiceClient serviceClient = serviceClientFactory.createServiceClient();

        if (!url.endsWith("/")) {
            url += "/";
        }

        this.url = url + serviceClient.getProvisioningServiceName();

        provisioningClient = serviceClient.getProvisioningService(this.url);

        xacmlPolicyQuery = makeStandardPAPQuery();
    }

    /**
     * Builds the policy request message to be sent to the PAP.
     * 
     * @return the policy request message to be sent to the PAP.
     */
    private static XACMLPolicyQueryType makeStandardPAPQuery() {

        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

        XACMLPolicyQueryType xacmlPolicyQuery = (XACMLPolicyQueryType) builderFactory.getBuilder(XACMLPolicyQueryType.TYPE_NAME_XACML20)
                                                                                     .buildObject(XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getNamespaceURI(),
                                                                                                  XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getLocalPart(),
                                                                                                  XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getPrefix());

        IssuerBuilder issuerBuilder = (IssuerBuilder) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);

        Issuer issuer = issuerBuilder.buildObject();

        issuer.setValue("ISSUER_STRING");
        issuer.setFormat(NameID.ENTITY);

        xacmlPolicyQuery.setIssuer(issuer);
        xacmlPolicyQuery.setIssueInstant(new DateTime());

        xacmlPolicyQuery.setVersion(SAMLVersion.VERSION_20);
        xacmlPolicyQuery.setID("_12345");
        xacmlPolicyQuery.setIssueInstant(new DateTime());

        // Set: this is a PAP query
        ExtensionsBuilder eBuilder = (ExtensionsBuilder) builderFactory.getBuilder(new QName(SAMLConstants.SAML20MD_NS,
                                                                                             Extensions.LOCAL_NAME,
                                                                                             SAMLConstants.SAML20MD_PREFIX));
        Extensions extensions = eBuilder.buildObject();

        xacmlPolicyQuery.setExtensions(extensions);

        return xacmlPolicyQuery;
    }

    /**
     * Makes a policy query to a PAP and retrieves the policies for the given request context.
     * 
     * @return a list of {@link PolicySetType} and {@link PolicyType} objects.
     * 
     * @throws RemoteException
     * @throws ServiceException
     */
    public List<XACMLObject> retrievePolicies() throws RemoteException, ServiceException {

        log.info("Requesting policies to remote PAP endpoint: " + url);

        Response response = provisioningClient.XACMLPolicyQuery(xacmlPolicyQuery);

        List<XACMLObject> resultList = getXACMLObjectList(response);

        return resultList;
    }

    /**
     * Extract the policies returned from the policy query.
     * 
     * @param response the saml response.
     * @return a list of {@link PolicySetType} and {@link PolicyType} objects.
     */
    private List<XACMLObject> getXACMLObjectList(Response response) {

        List<XACMLObject> responseList = new LinkedList<XACMLObject>();

        for (Assertion assertion : response.getAssertions()) {
            for (Statement statement : assertion.getStatements()) {
                String statementLocalName = statement.getSchemaType().getLocalPart();

                if (XACMLPolicyStatementType.TYPE_LOCAL_NAME.equals(statementLocalName)) {

                    XACMLPolicyStatementType policyStatement = (XACMLPolicyStatementType) statement;

                    for (PolicySetType policySet : policyStatement.getPolicySets()) {
                        responseList.add(new PolicySetTypeString(policySet));
                    }
                    for (PolicyType policy : policyStatement.getPolicies()) {
                        responseList.add(new PolicyTypeString(policy));
                    }
                }
            }
        }
        return responseList;
    }
}
