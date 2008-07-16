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
 * File : ProvisioningServiceUtils.java
 *
 * Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */

package org.glite.authz.pap.provisioning;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.glite.authz.pap.provisioning.exceptions.MissingIssuerException;
import org.glite.authz.pap.provisioning.exceptions.VersionMismatchException;
import org.glite.authz.pap.provisioning.exceptions.WrongFormatIssuerException;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
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
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;
import org.opensaml.xacml.profile.saml.impl.XACMLPolicyStatementTypeImplBuilder;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * @author Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */
public class ProvisioningServiceUtils {

    private static Logger logger = LoggerFactory
	    .getLogger(ProvisioningServiceUtils.class);

    public static String xmlObjectToString(XMLObject xmlObject) {

	try {

	    MarshallerFactory marshallerFactory = Configuration
		    .getMarshallerFactory();
	    Marshaller queryMarshaller = marshallerFactory
		    .getMarshaller(xmlObject);
	    Element queryElement = queryMarshaller.marshall(xmlObject);
	    return XMLHelper.nodeToString(queryElement);

	} catch (MarshallingException e) {
	    throw new RuntimeException(e);
	}

    }

    // TODO this method is too long, should be split
    public static Response createResponse(XACMLPolicyQueryType inResponseTo,
	    List<XACMLObject> policyObjects) {

	// get a builder factory
	XMLObjectBuilderFactory builderFactory = Configuration
		.getBuilderFactory();

	/* prepare the Response object to return */

	// build a response object
	ResponseBuilder responseBuilder = (ResponseBuilder) builderFactory
		.getBuilder(Response.DEFAULT_ELEMENT_NAME);
	Response response = responseBuilder.buildObject();

	// set a few attributes for the response
	response.setID("_" + UUID.randomUUID().toString());
	response.setVersion(SAMLVersion.VERSION_20);
	response.setIssueInstant(new DateTime());
	response.setInResponseTo(inResponseTo.getID());

	/* add the Assertion element */

	// build an assertion object
	AssertionBuilder assertionBuilder = (AssertionBuilder) builderFactory
		.getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
	Assertion assertion = assertionBuilder.buildObject();

	// set a few attributes for the assertion
	assertion.setID("_" + UUID.randomUUID().toString());
	assertion.setVersion(SAMLVersion.VERSION_20);
	assertion.setIssueInstant(new DateTime());

	// build an issuer object
	IssuerBuilder issuerBuilder = (IssuerBuilder) builderFactory
		.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
	Issuer issuer = issuerBuilder.buildObject();

	// TODO here goes the name of the PAP
	issuer.setValue("test");

	assertion.setIssuer(issuer);

	/* build policy statements objects */

	XACMLPolicyStatementTypeImplBuilder policyStatementBuilder = (XACMLPolicyStatementTypeImplBuilder) builderFactory
		.getBuilder(XACMLPolicyStatementType.TYPE_NAME_XACML20);

	XACMLPolicyStatementType policyStatement = policyStatementBuilder
		.buildObject(Statement.DEFAULT_ELEMENT_NAME,
			XACMLPolicyStatementType.TYPE_NAME_XACML20);
	
	Iterator<XACMLObject> iterator = policyObjects.iterator();

	while (iterator.hasNext()) {

	    XACMLObject xacmlObject = iterator.next();

	    if (xacmlObject instanceof PolicySetType) {

		PolicySetType policySet = (PolicySetType) xacmlObject;
		policyStatement.getPolicySets().add(policySet);

	    } else {

		PolicyType policy = (PolicyType) xacmlObject;
		policyStatement.getPolicies().add(policy);

	    }

	    // add the statement to the assertion
	    assertion.getStatements().add(policyStatement);
	}

	// add the assertion to the response
	response.getAssertions().add(assertion);

	/* add the Status element */

	// build a status object
	StatusBuilder statusBuilder = (StatusBuilder) builderFactory
		.getBuilder(Status.DEFAULT_ELEMENT_NAME);
	Status status = statusBuilder.buildObject();

	// build a status code object
	StatusCodeBuilder statusCodeBuilder = (StatusCodeBuilder) builderFactory
		.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
	StatusCode statusCode = statusCodeBuilder.buildObject();

	statusCode.setValue(StatusCode.SUCCESS_URI);

	status.setStatusCode(statusCode);

	response.setStatus(status);

	return response;
    }

    public static Response createResponse(XACMLPolicyQueryType inResponseTo,
	    Exception e) {

	// get a builder factory
	XMLObjectBuilderFactory builderFactory = Configuration
		.getBuilderFactory();

	/* prepare the response */

	// TODO part of the response creation are repeated in the two create,
	// share
	// build a response object
	ResponseBuilder responseBuilder = (ResponseBuilder) builderFactory
		.getBuilder(Response.DEFAULT_ELEMENT_NAME);
	Response response = responseBuilder.buildObject();

	// set a few attributes for the response
	response.setID("_" + UUID.randomUUID().toString());
	response.setVersion(SAMLVersion.VERSION_20);
	response.setIssueInstant(new DateTime());
	response.setInResponseTo(inResponseTo.getID());

	/* add the Status element */

	// build a status object
	StatusBuilder statusBuilder = (StatusBuilder) builderFactory
		.getBuilder(Status.DEFAULT_ELEMENT_NAME);
	Status status = statusBuilder.buildObject();

	// build a status code object
	StatusCodeBuilder statusCodeBuilder = (StatusCodeBuilder) builderFactory
		.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
	StatusCode statusCode = statusCodeBuilder.buildObject();

	// TODO now discriminates by exception but the code sucks, you happy?

	if (e instanceof VersionMismatchException) {

	    /* set status code */

	    statusCode.setValue(StatusCode.VERSION_MISMATCH_URI);

	} else if (e instanceof MissingIssuerException) {

	    /* set status code */

	    statusCode.setValue(StatusCode.REQUESTER_URI);

	    /* set status message with some details */

	    StatusMessageBuilder statusMessageBuilder = (StatusMessageBuilder) builderFactory
		    .getBuilder(StatusMessage.DEFAULT_ELEMENT_NAME);
	    StatusMessage statusMessage = statusMessageBuilder.buildObject();
	    statusMessage.setMessage("The Issuer element MUST be present.");

	    // add StatusMessage to Status
	    status.setStatusMessage(statusMessage);

	} else if (e instanceof WrongFormatIssuerException) {

	    /* set status code */

	    statusCode.setValue(StatusCode.REQUESTER_URI);

	    /* set status message with some details */

	    StatusMessageBuilder statusMessageBuilder = (StatusMessageBuilder) builderFactory
		    .getBuilder(StatusMessage.DEFAULT_ELEMENT_NAME);
	    StatusMessage statusMessage = statusMessageBuilder.buildObject();
	    statusMessage.setMessage("The Format attribute of the Issuer "
		    + "element must be " + NameID.ENTITY);

	    // add StatusMessage to Status
	    status.setStatusMessage(statusMessage);

	} else {

	    /* set status code */

	    statusCode.setValue(StatusCode.RESPONDER_URI);

	}

	// add StatusCode to Status
	status.setStatusCode(statusCode);

	response.setStatus(status);

	return response;
    }

    public static void checkQuery(XACMLPolicyQueryType query)
	    throws VersionMismatchException, MissingIssuerException,
	    WrongFormatIssuerException {

	/* check the version attribute is for a SAML V2.0 query */

	if (query.getVersion() != SAMLVersion.VERSION_20) {
	    throw new VersionMismatchException();
	}

	/* TODO check issue instant */

	/* check the issuer is present and has the expected format */

	Issuer issuer = query.getIssuer();

	if (issuer == null) {
	    throw new MissingIssuerException();
	}

	String issuerFormat = issuer.getFormat();

	if (issuerFormat != null && !issuerFormat.equals(NameID.ENTITY))
	    throw new WrongFormatIssuerException(issuerFormat);

	// TODO Check that the issuer is the same as in the transport

    }

}
