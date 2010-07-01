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

package org.glite.authz.pap.ui.cli.samlclient;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.ui.cli.policymanagement.XACMLPolicyCLIUtils;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.opensaml.xacml.profile.saml.impl.XACMLPolicyQueryTypeImplBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;

public class SAMLClient extends SAMLClientCLI {
    
    private static final String[] commandNameValues = { "test-client", "tc" };
    private static final String OPT_PDP = "pdp";
    private static final String OPT_PDP_DESCRIPTION = "Send a PDP query (otherwise a PAP query is sent).";
    private static final String DESCRIPTION = "Test client perfoming a PAP (default) or PDP query.";
    private static final String USAGE = "[options]";
    
    public SAMLClient() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }
    
    @Override
    @SuppressWarnings("static-access")
    protected Options defineCommandOptions() {
	    Options options = new Options();
	    
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_PDP_DESCRIPTION).create(OPT_PDP));
        
        return options;
	}

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        
        boolean isPAPQuery = true;
        
        if (commandLine.hasOption(OPT_PDP)) {
            isPAPQuery = false;
        }
        
        XACMLPolicyCLIUtils.initOpenSAMLAndAttributeWizard();
        
        XACMLPolicyQueryType query = buildSamlXacmlQuery("IssuerString", isPAPQuery);
    	
    	Response response = samlClient.XACMLPolicyQuery(query);

    	if (verboseMode) {
    	    System.out.println(XMLObjectHelper.toString(response));
    	} else {
    	    System.out.println("Received response");
    	}
    	
        return ExitStatus.SUCCESS.ordinal();
        
    }
    
    private static XACMLPolicyQueryType buildSamlXacmlQuery(String issuerString, boolean isPAPQuery) {

        XMLObjectBuilderFactory bf = Configuration.getBuilderFactory();

        XACMLPolicyQueryTypeImplBuilder builder = (XACMLPolicyQueryTypeImplBuilder) bf.getBuilder(XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20);

        // TODO: submit bug to opensaml ?
        XACMLPolicyQueryType policyQuery = builder.buildObject(XACMLPolicyQueryType.DEFAULT_ELEMENT_NAME_XACML20.getNamespaceURI(),
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
            ExtensionsBuilder eBuilder = (ExtensionsBuilder) bf.getBuilder(new QName(SAMLConstants.SAML20MD_NS,
                Extensions.LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX));
            Extensions extensions = eBuilder.buildObject();

            policyQuery.setExtensions(extensions);

        }
        return policyQuery;
    }
}
