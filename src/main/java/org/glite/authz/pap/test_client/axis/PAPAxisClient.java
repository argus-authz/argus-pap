package org.glite.authz.pap.test_client.axis;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.ServiceClientFactory;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.opensaml.xacml.profile.saml.impl.XACMLPolicyQueryTypeImplBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea
 * @author Valerio
 * 
 */

public class PAPAxisClient {
    
    private Response response;

    static final Logger log = LoggerFactory.getLogger(PAPAxisClient.class);

    static final String samlNamespace = "urn:oasis:names:tc:xacml:2.0:profile:saml2.0:v2:schema:protocol";

    private String url;

    private String certificate;

    private String key;

    private String keyPasswd;

    private boolean papMode;

    protected CommandLineParser parser = new GnuParser();

    protected HelpFormatter helpFormatter = new HelpFormatter();

    protected Options options;

    private void setupDefaults() {

        papMode = false;
        //url = "https://pbox3.cnaf.infn.it:8443/glite-authz-pap/services/ProvisioningService";
        url = "https://localhost:8150/glite-authz-pap/services/ProvisioningService";

    }

    @SuppressWarnings("static-access")
    private void setupCLParser() {

        options = new Options();

        options.addOption(OptionBuilder.withLongOpt("help").withDescription("Displays helps and exits.")
                .create("h"));

        options.addOption(OptionBuilder.withLongOpt("url").withDescription(
                "Specifies the target PAP endpoint that is contacted.").hasArg().create("url"));

        options.addOption(OptionBuilder.withLongOpt("cert").withDescription(
                "Specifies non-standard user certificate.").hasArg().create("cert"));

        options.addOption(OptionBuilder.withLongOpt("key").withDescription(
                "Specifies non-standard user private key.").hasArg().create("key"));

        options.addOption(OptionBuilder.withLongOpt("password").withDescription(
                "Specifies a password that is used to decrypt the user's private key.").hasArg().create(
                "password"));

        options.addOption(OptionBuilder.withLongOpt("url").withDescription(
                "Specifies the target PAP endpoint that is contacted.").hasArg().create("url"));

        options.addOption(OptionBuilder.withLongOpt("pap-mode").withDescription(
                "When set, the client behaves as a PAP client instead of a PDP client.").create(
                "pap_mode"));

    }

    private void parseArguments(String[] args) {

        setupDefaults();

        try {

            CommandLine line = parser.parse(options, args);

            if (line.hasOption("h"))
                printHelpMessageAndExit(0);

            if (line.hasOption("url"))
                url = line.getOptionValue("url");

            if (line.hasOption("cert"))
                certificate = line.getOptionValue("cert");

            if (line.hasOption("key"))
                key = line.getOptionValue("key");

            if (line.hasOption("password"))
                keyPasswd = line.getOptionValue("password");

            if (line.hasOption("pap-mode"))
                papMode = true;

        } catch (ParseException e) {

            log.error("Error parsing command line arguments: " + e.getMessage());
            printHelpMessageAndExit(1);
        }

    }

    private void printHelpMessageAndExit(int exitStatus) {

        helpFormatter.printHelp("PAPAxisClient", options);
        System.exit(exitStatus);

    }

    private void configureOpenSAML() {

        try {
            DefaultBootstrap.bootstrap();

        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    public XACMLPolicyQueryType buildPolicyQuery(String issuerString, boolean isPAPQuery) {

        XMLObjectBuilderFactory bf = Configuration.getBuilderFactory();

        XACMLPolicyQueryTypeImplBuilder builder = (XACMLPolicyQueryTypeImplBuilder) bf
                .getBuilder(XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20);

        // TODO: submit bug to opensaml ?
        XACMLPolicyQueryType policyQuery = builder.buildObject(
                XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getNamespaceURI(),
                XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getLocalPart(),
                XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getPrefix());

        IssuerBuilder issuerBuilder = (IssuerBuilder) bf.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);

        Issuer issuer = issuerBuilder.buildObject();

        issuer.setValue(issuerString);
        issuer.setFormat(NameID.ENTITY);

        policyQuery.setIssuer(issuer);
        policyQuery.setIssueInstant(new DateTime());

        if (isPAPQuery) {

            // TODO: submit bug to opensaml?
            ExtensionsBuilder eBuilder = (ExtensionsBuilder) bf.getBuilder(new QName(
                    SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX));
            Extensions extensions = eBuilder.buildObject();

            policyQuery.setExtensions(extensions);

        }
        return policyQuery;

    }

    private void printXMLObject(String prefix, XMLObject o) throws MarshallingException {

        MarshallerFactory mf = Configuration.getMarshallerFactory();
        Marshaller marshaller = mf.getMarshaller(o);
        log.info(prefix + " " + XMLHelper.nodeToString(marshaller.marshall(o)));
        System.out.println(XMLObjectHelper.toString(o));
    }

    public PAPAxisClient(String[] args) throws RemoteException, ServiceException, MarshallingException,
            TransformerException {

        setupCLParser();

        parseArguments(args);

        configureOpenSAML();

        XACMLPolicyQueryType policyQuery = buildPolicyQuery("IssuerString", papMode);

        printXMLObject("PolicyQuery:", policyQuery);

        /* get the client */
        ServiceClientFactory serviceClientFactory = ServiceClientFactory.getServiceClientFactory();
        ServiceClient serviceClient = serviceClientFactory.createServiceClient();
        
        if (certificate != null)
            serviceClient.setClientCertificate(certificate);

        if (key != null)
            serviceClient.setClientPrivateKey(key);

        if (keyPasswd != null)
            serviceClient.setClientPrivateKeyPassword(keyPasswd);
        
        Provisioning provisioningClient = serviceClient.getProvisioningService(this.url);

        /* call the service */
        response = provisioningClient.XACMLPolicyQuery(policyQuery);

        printXMLObject("Response:", response);

    }
    
    public Response getReponse() {
        return response;
    }

    /**
     * @throws ServiceException
     * @throws RemoteException
     * @throws MarshallingException
     * @throws TransformerException
     */
    public static void main(String[] args) throws RemoteException, ServiceException,
            MarshallingException, TransformerException {

        new PAPAxisClient(args);

    }

}
