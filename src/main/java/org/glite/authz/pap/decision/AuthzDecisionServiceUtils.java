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
 * File : AuthzDecisionServiceUtils.java
 *
 * Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */
 
package org.glite.authz.pap.decision;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.provisioning.ProvisioningService;
import org.glite.authz.pap.provisioning.ProvisioningServiceUtils;
import org.glite.authz.pap.provisioning.exceptions.MissingIssuerException;
import org.glite.authz.pap.provisioning.exceptions.VersionMismatchException;
import org.glite.authz.pap.provisioning.exceptions.WrongFormatIssuerException;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Statement;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml2.core.impl.StatusMessageBuilder;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionStatementType;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;
import org.opensaml.xacml.profile.saml.impl.XACMLAuthzDecisionStatementTypeImplBuilder;
import org.opensaml.xacml.profile.saml.impl.XACMLPolicyStatementTypeImplBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
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

import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

/**
 * @author Valerio Venturi <valerio.venturi@cnaf.infn.it>
 *
 */
public class AuthzDecisionServiceUtils {

    private static final Logger logger = 
	LoggerFactory.getLogger(AuthzDecisionServiceUtils.class);

    private static XMLObjectBuilderFactory builderFactory = 
	    Configuration.getBuilderFactory();
    
    // TODO going to be moved to a common tuils for pap e pdp
    private static Response createResponse(RequestAbstractType inResponseTo) {
	
	// build a response object
	ResponseBuilder responseBuilder = 
	    (ResponseBuilder) builderFactory.getBuilder(Response.DEFAULT_ELEMENT_NAME);
	Response response = responseBuilder.buildObject();

	// set a few attributes for the response
	response.setID("_" + UUID.randomUUID().toString());
	response.setVersion(SAMLVersion.VERSION_20);
	response.setIssueInstant(new DateTime());
	response.setInResponseTo(inResponseTo.getID());

	return response;
    }

    // TODO going to be moved to a common utils for pap e pdp
    private static Assertion prepareAssertion() {

	// build an assertion object
	AssertionBuilder assertionBuilder = 
	    (AssertionBuilder) builderFactory.getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
	Assertion assertion = assertionBuilder.buildObject();

	// set a few attributes for the assertion
	assertion.setID("_" + UUID.randomUUID().toString());
	assertion.setVersion(SAMLVersion.VERSION_20);
	assertion.setIssueInstant(new DateTime());

	// build an issuer object
	IssuerBuilder issuerBuilder = 
	    (IssuerBuilder) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
	Issuer issuer = issuerBuilder.buildObject();

	// TODO here goes the name of the PAP
	issuer.setValue("test");

	assertion.setIssuer(issuer);

	return assertion;
    }

    // TODO going to be moved to a common utils for pap e pdp
    private static Status prepareStatus() {

	// build a status object
	StatusBuilder statusBuilder = 
	    (StatusBuilder) builderFactory.getBuilder(Status.DEFAULT_ELEMENT_NAME);
	Status status = statusBuilder.buildObject();

	// build a status code object
	StatusCodeBuilder statusCodeBuilder = 
	    (StatusCodeBuilder) builderFactory.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
	StatusCode statusCode = statusCodeBuilder.buildObject();

	statusCode.setValue(StatusCode.SUCCESS_URI);

	status.setStatusCode(statusCode);

	return status;
    }

    // TODO going to be moved to a common utils for pap e pdp
    private static Status prepareStatus(Exception e) {

	// build a status object
	StatusBuilder statusBuilder = 
	    (StatusBuilder) builderFactory.getBuilder(Status.DEFAULT_ELEMENT_NAME);
	Status status = statusBuilder.buildObject();

	// build a status code object
	StatusCodeBuilder statusCodeBuilder = 
	    (StatusCodeBuilder) builderFactory.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
	StatusCode statusCode = statusCodeBuilder.buildObject();

	if (e instanceof VersionMismatchException) {

	    statusCode.setValue(StatusCode.VERSION_MISMATCH_URI);

	} else if (e instanceof MissingIssuerException | 
		e instanceof WrongFormatIssuerException) {

	    // set the status code
	    statusCode.setValue(StatusCode.REQUESTER_URI);

	    // set status message
	    if (e.getMessage() != null) {

		StatusMessageBuilder statusMessageBuilder = 
		    (StatusMessageBuilder) builderFactory.getBuilder(StatusMessage.DEFAULT_ELEMENT_NAME);
		
		StatusMessage statusMessage = 
		    statusMessageBuilder.buildObject();
		statusMessage.setMessage(e.getMessage());

		// add StatusMessage to Status
		status.setStatusMessage(statusMessage);

	    }

	} else {

	    /* set status code */
	    statusCode.setValue(StatusCode.RESPONDER_URI);

	}

	// add StatusCode to Status
	status.setStatusCode(statusCode);

	return status;
    }
    
    public static Response createResponse(RequestAbstractType inResponseTo, 
	    ResponseType xacmlResponse) {

	Response response = createResponse(inResponseTo);
	
	Assertion assertion = prepareAssertion();
	
	// build authz decision statement
	XACMLAuthzDecisionStatementTypeImplBuilder authzDecisionStatementBuilder = 
	    (XACMLAuthzDecisionStatementTypeImplBuilder) builderFactory.getBuilder(XACMLAuthzDecisionStatementType.TYPE_NAME_XACML20);

	XACMLAuthzDecisionStatementType authzDecisionStatement = 
	    authzDecisionStatementBuilder.buildObject(XACMLAuthzDecisionStatementType.DEFAULT_ELEMENT_NAME, 
		    XACMLAuthzDecisionStatementType.TYPE_NAME_XACML20);
	
	authzDecisionStatement.setResponse(xacmlResponse);
	
	// add the statement to the assertion
	assertion.getStatements().add(authzDecisionStatement);
	
	// add the assertion to the response
	response.getAssertions().add(assertion);
	
	Status status = prepareStatus();
	
	// add the status to the response
	response.setStatus(status);

	return response;
    }

    public static Response createResponse(RequestAbstractType inResponseTo,
	    Exception e) {

	Response response = createResponse(inResponseTo);
	
	Status status = prepareStatus(e);
	
	response.setStatus(status);

	return response;
    }

    public static RequestCtx toRequestCtx(RequestType opensamlRequest) 
    	throws MarshallingException, ParsingException {

	logger.debug("Converting a RequestType to a RequestCtx object");

	Element queryElement = 	XMLObjectHelper.getDOM(opensamlRequest);

	RequestCtx sunxacmlRequest = RequestCtx.getInstance(queryElement);
	
	return sunxacmlRequest;
	
    }

    public static ResponseType toRequestType(ResponseCtx sunxacmlResponse) 
    	throws UnsupportedEncodingException, XMLParserException {
	
	logger.debug("Converting a ResponseCtx to a ResponseType object");
	
	// ResponseCtx to outputStream
	
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
	
	sunxacmlResponse.encode(outputStream, null); 
	
	// TODO that's nasty, there must be a way to produce xacml 2.0 responses
	
	String sunxacmlResponseAsString = new String(outputStream.toByteArray());
	
	String sunxacmlResponseAsStringReplaced = 
	    sunxacmlResponseAsString.replace("urn:oasis:names:tc:xacml:3.0:schema:os", 
		    "urn:oasis:names:tc:xacml:2.0:context:schema:os");
	
	// bytes to Element
	
	InputStream inputStream = 
	    new ByteArrayInputStream(sunxacmlResponseAsStringReplaced.getBytes());
	
	BasicParserPool ppMgr = new BasicParserPool();
	ppMgr.setNamespaceAware(true);

	Document responseDocument = ppMgr.parse(inputStream);

	Element responseElement = responseDocument.getDocumentElement();

	// Element to ResponseType
	
	ResponseType opensamlXacmlResponse = 
	    (ResponseType) XMLObjectHelper.buildXMLObject(responseElement);
	
	return opensamlXacmlResponse;
	
    }
  
}
