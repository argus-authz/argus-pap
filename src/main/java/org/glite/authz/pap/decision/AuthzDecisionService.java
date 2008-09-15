/**
 *
 * Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)
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
 *
 * File : AuthzDecisionService.java
 *
 * Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */

package org.glite.authz.pap.decision;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.glite.authz.pap.provisioning.ProvisioningService;
import org.glite.authz.pap.provisioning.ProvisioningServiceUtils;
import org.opensaml.Configuration;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.impl.CurrentEnvModule;

/**
 * @author Valerio Venturi <valerio.venturi@cnaf.infn.it>
 *
 */
public class AuthzDecisionService {

    final Logger logger = LoggerFactory.getLogger(ProvisioningService.class);

    public Response XACMLAuthzDecisionQuery(XACMLAuthzDecisionQueryType query)
	    throws java.rmi.RemoteException {
	
	logger.debug("Received " + ProvisioningServiceUtils.xmlObjectToString(query));
	
	RequestType opensamlRequest = query.getRequest();
	
	/* convert the openSAML Request a sunxacml Request */

	logger.debug("OpenSAML RequestType is " + 
		ProvisioningServiceUtils.xmlObjectToString(opensamlRequest));
	
	logger.debug("Converting to a RequestCtx object");
	
	MarshallerFactory marshallerFactory = 
	    Configuration.getMarshallerFactory();
	Marshaller queryMarshaller = marshallerFactory.getMarshaller(opensamlRequest);
	
	Element queryElement = null;
	
	try {
	    queryElement = queryMarshaller.marshall(opensamlRequest);
	} catch (MarshallingException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	}

	RequestCtx sunxacmlRequest = null;
	
	try {
	    sunxacmlRequest = RequestCtx.getInstance(queryElement);
	} catch (ParsingException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	}

	{
	
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    
	    try {
		sunxacmlRequest.encode(outputStream, null);
	    } catch (UnsupportedEncodingException e) {
		return AuthzDecisionServiceUtils.createResponse(query, e);
	    }
	
	    logger.debug("Sun-XACLM RequestCtx is " + new String(outputStream.toByteArray()));
	    
	}
	    
	    
	/* configure the policy evaluation engine */
	
	logger.debug("Configuring policy decision point");
	
	// TODO got to write a policy finder for our repository
	
	Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
	//policyModules.add(policyModule);
	
	PolicyFinder policyFinder = new PolicyFinder();
	policyFinder.setModules(policyModules);
	
	CurrentEnvModule envModule = new CurrentEnvModule();
	
	List<AttributeFinderModule> attributeFinderModules = 
	    new ArrayList<AttributeFinderModule>();
	attributeFinderModules.add(envModule);
	
	AttributeFinder attributeFinder = new AttributeFinder();
	attributeFinder.setModules(attributeFinderModules);
	
	PDPConfig pdpConfig = 
	    new PDPConfig(attributeFinder,policyFinder, null, null);
	
	PDP pdp = new PDP(pdpConfig);
	
	/* call the policy evaluation engine */
	
	logger.debug("Calling policy decision point");
	
	ResponseCtx sunxacmlResponse = pdp.evaluate(sunxacmlRequest);
	
	/* now back to openSAML */

	logger.debug("Converting to a ResponseType object");
	
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
	
	try {
	    sunxacmlResponse.encode(outputStream, null);
	} catch (UnsupportedEncodingException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	}
	
	// TODO workaround, there must be a way to produce xacml 2.0 responses
	
	String sunxacmlResponseAsString = 
	    new String(outputStream.toByteArray());
	
	logger.debug(sunxacmlResponseAsString);
	
	String sunxacmlResponseAsStringReplaced = 
	    sunxacmlResponseAsString.replace("urn:oasis:names:tc:xacml:3.0:schema:os", 
		    "urn:oasis:names:tc:xacml:2.0:context:schema:os");
	
	logger.debug(sunxacmlResponseAsStringReplaced);
	
	InputStream inputStream = 
	    new ByteArrayInputStream(sunxacmlResponseAsStringReplaced.getBytes());
	
	BasicParserPool ppMgr = new BasicParserPool();
	ppMgr.setNamespaceAware(true);

	Document responseDocument = null;
	
	try {
	    responseDocument = ppMgr.parse(inputStream);
	} catch (XMLParserException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	}

	Element responseElement = responseDocument.getDocumentElement();

	UnmarshallerFactory unmarshallerFactory = 
	    Configuration.getUnmarshallerFactory();
	Unmarshaller responseUnmarshaller = 
	    unmarshallerFactory.getUnmarshaller(ResponseType.DEFAULT_ELEMENT_NAME);
	
	ResponseType opensamlXacmlResponse = null;
	
	try {
	    opensamlXacmlResponse = 
		(ResponseType) responseUnmarshaller.unmarshall(responseElement);
	} catch (UnmarshallingException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	}
	
	/*  */
	
	Response response = 
	    AuthzDecisionServiceUtils.createResponse(query, opensamlXacmlResponse); 
		
	logger.debug("Sending " + ProvisioningServiceUtils.xmlObjectToString(response));
	
	return response; 
    }
    
}
