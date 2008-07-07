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

import java.io.StringReader;
import java.util.Iterator;
import java.util.UUID;

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
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
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
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Valerio Venturi <valerio.venturi@cnaf.infn.it>
 *
 */
public class ProvisioningServiceUtils {

  private static Logger logger = LoggerFactory.getLogger( ProvisioningServiceUtils.class );

  public static String xmlObjectToString( XMLObject xmlObject ) {

    try {

      MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
      Marshaller queryMarshaller = marshallerFactory.getMarshaller( xmlObject );
      Element queryElement = queryMarshaller.marshall( xmlObject );
      return XMLHelper.nodeToString( queryElement );

    }
    catch ( MarshallingException e ) {
      throw new RuntimeException( e );
    }

  }

  public static Response createResponse( XACMLPolicyQueryType inResponseTo ,
                                         java.util.List<XACMLObject> policyObjects ) {

    // get a builder factory
    XMLObjectBuilderFactory builderFactory = 
      Configuration.getBuilderFactory();

    // build a response object
    ResponseBuilder responseBuilder = 
      (ResponseBuilder) builderFactory.getBuilder( Response.DEFAULT_ELEMENT_NAME );
    Response response = responseBuilder.buildObject();

    // set a few attributes for the response
    response.setID( "_" + UUID.randomUUID().toString() );
    response.setVersion( SAMLVersion.VERSION_20 );
    response.setIssueInstant( new DateTime() );
    response.setInResponseTo( inResponseTo.getID() );
    
    // build an assertion object
    AssertionBuilder assertionBuilder = 
      (AssertionBuilder) builderFactory.getBuilder( Assertion.DEFAULT_ELEMENT_NAME );
    Assertion assertion = assertionBuilder.buildObject();
    
    // set a few attributes for the assertion
    assertion.setID( "_" + UUID.randomUUID().toString() );
    assertion.setVersion( SAMLVersion.VERSION_20 );
    assertion.setIssueInstant( new DateTime() );
    
    // build an issuer object
    IssuerBuilder issuerBuilder = 
      (IssuerBuilder) builderFactory.getBuilder( Issuer.DEFAULT_ELEMENT_NAME );
    Issuer issuer = issuerBuilder.buildObject();
    
    // set the issuer value
    issuer.setValue( "test" );

    assertion.setIssuer( issuer );

    /* build policy statements objects */

    XACMLPolicyStatementTypeImplBuilder policyStatementBuilder = 
      (XACMLPolicyStatementTypeImplBuilder) builderFactory.getBuilder( XACMLPolicyStatementType.TYPE_NAME_XACML20 );
    
    Iterator<XACMLObject> iterator = policyObjects.iterator();
    
    while(iterator.hasNext()) {
    
      // build the policy statement
      XACMLPolicyStatementType policyStatement = 
        policyStatementBuilder.buildObject( Statement.DEFAULT_ELEMENT_NAME , XACMLPolicyStatementType.TYPE_NAME_XACML20 );
    
      // the objct is either a policy set or a policy
      
      XACMLObject xacmlObject = iterator.next();
      
      if(xacmlObject instanceof PolicySetType) {
        
        PolicySetType policySet = (PolicySetType) xacmlObject; 
        policyStatement.getPolicySets().add( policySet );
        
      } else {
        
        PolicyType policy = (PolicyType) xacmlObject;
        policyStatement.getPolicies().add( policy );
      }

      assertion.getStatements().add( policyStatement );
    }
    
    response.getAssertions().add( assertion );

    //  build a status object
    StatusBuilder statusBuilder = 
      (StatusBuilder) builderFactory.getBuilder( Status.DEFAULT_ELEMENT_NAME );
    Status status = statusBuilder.buildObject();

    //  build a status code builder object
    StatusCodeBuilder statusCodeBuilder = 
      (StatusCodeBuilder) builderFactory.getBuilder( StatusCode.DEFAULT_ELEMENT_NAME );
    StatusCode statusCode = statusCodeBuilder.buildObject();
    
    statusCode.setValue( StatusCode.SUCCESS_URI );

    status.setStatusCode( statusCode );

    response.setStatus( status );

    return response;
  }

  public static Response createResponse( XACMLPolicyQueryType inResponseTo ,
                                         Exception e ) {

    // get a builder factory
    XMLObjectBuilderFactory builderFactory = 
      Configuration.getBuilderFactory();

    // build a response object
    ResponseBuilder responseBuilder = 
      (ResponseBuilder) builderFactory.getBuilder( Response.DEFAULT_ELEMENT_NAME );
    Response response = responseBuilder.buildObject();

    // set a few attributes for the response
    response.setID( "_" + UUID.randomUUID().toString() );
    response.setVersion( SAMLVersion.VERSION_20 );
    response.setIssueInstant( new DateTime() );
    response.setInResponseTo( inResponseTo.getID() );

    // build a status object
    StatusBuilder statusBuilder = 
      (StatusBuilder) builderFactory.getBuilder( Status.DEFAULT_ELEMENT_NAME );
    Status status = statusBuilder.buildObject();

    // build a status code builder object
    StatusCodeBuilder statusCodeBuilder = 
      (StatusCodeBuilder) builderFactory.getBuilder( StatusCode.DEFAULT_ELEMENT_NAME );
    StatusCode statusCode = statusCodeBuilder.buildObject();
    
    statusCode.setValue( StatusCode.RESPONDER_URI ); // TODO must discriminate by exception

    status.setStatusCode( statusCode );

    response.setStatus( status );

    return response;
  }

  public static void checkQuery( XACMLPolicyQueryType query )
      throws VersionMismatchException, MissingIssuerException, WrongFormatIssuerException {

    /* check the version attribute is for a SAML V2.0 query */
    
    if ( query.getVersion() != SAMLVersion.VERSION_20 ) {
      throw new VersionMismatchException();
    }

    // TODO check issue instant

    /* check the issuer is present and has the expected format */
    
    Issuer issuer = query.getIssuer();

    if ( issuer == null ) {
      throw new MissingIssuerException();
    }

    /* the format MUST be omitted or have the entity value */
    
    String issuerFormat = issuer.getFormat();

    if ( issuerFormat != null && issuerFormat != NameID.ENTITY ) {
      throw new WrongFormatIssuerException(issuerFormat);
    }

  }
  

  public static PolicySetType createPolicySet( Element policySet ) {

    BasicParserPool basicParserPool = new BasicParserPool();
    basicParserPool.setNamespaceAware( true );

    String policySetAsString = XMLHelper.nodeToString( policySet );
    StringReader stringReader = new StringReader( policySetAsString );
    
    Element policySetElement = null;
    
    try {
      Document policySetDocument = basicParserPool.parse( stringReader ); 
      policySetElement = policySetDocument.getDocumentElement();
    } 
    catch ( XMLParserException e ) {
      logger.error( e.getMessage() );
      throw new Error( e );
    }
    
    UnmarshallerFactory unmarshallerFactory = 
      Configuration.getUnmarshallerFactory();
    Unmarshaller unmarshaller = 
      unmarshallerFactory.getUnmarshaller( policySetElement );

    PolicySetType policySetType = null;

    try {
      policySetType = (PolicySetType) unmarshaller.unmarshall( policySetElement );
    } 
    catch ( UnmarshallingException e ) {
      logger.error( e.getMessage() );
      throw new Error( e );
    }

    return policySetType;
  }

}
