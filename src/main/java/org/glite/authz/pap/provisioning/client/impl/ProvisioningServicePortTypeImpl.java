/**************************************************************************

 Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 File : ProvisioningServicePortTypeImpl.java

 Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.provisioning.client.impl;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.glite.authz.pap.provisioning.axis.DeserializerFactory;
import org.glite.authz.pap.provisioning.axis.SerializerFactory;
import org.glite.authz.pap.provisioning.client.ProvisioningServicePortType;
import org.glite.security.trustmanager.axis.AXISSocketFactory;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;

public class ProvisioningServicePortTypeImpl
    implements ProvisioningServicePortType {

  private String url;

  protected ProvisioningServicePortTypeImpl( String url ) {
    this.url = url;
  }

  public Response xacmlPolicyQuery( XACMLPolicyQueryType xacmlPolicyQuery )
      throws ServiceException, RemoteException {

    /* TODO this client is supposed to be used to contact PAPs, 
     * so check the proper extensions is present */
    
    /* tell Axis to use trustmanager secure socket factory and set 
     * the properties for the cert and key */

    AxisProperties.setProperty("axis.socketSecureFactory",
      "org.glite.security.trustmanager.axis.AXISSocketFactory");

    Properties properties = AXISSocketFactory.getCurrentProperties();

    // TODO will get cert and key form the configuration, with those as default
    properties.setProperty( "sslCertFile" , "/etc/grid-security/hostcert.pem" );
    properties.setProperty( "sslKey" , "/etc/grid-security/hostkey.pem" );

    AXISSocketFactory.setCurrentProperties(properties);

    /* instantiate the axis service */

    Service service = new Service();

    Call call = (Call) service.createCall();

    call.setTargetEndpointAddress( url );

    call.setOperationName( new QName( "http://www.example.org" , "method" ) );
    call.setOperationStyle( Style.DOCUMENT );
    call.setOperationUse( Use.LITERAL );

    /* register custom serializer and deserializer */
    
    QName xacmlPolicyQueryQName = 
      new QName("urn:oasis:names:tc:xacml:2.0:profile:saml2.0:v2:schema:protocol", 
                "XACMLPolicyQuery");
    
    QName responseQName =
      new QName("urn:oasis:names:tc:SAML:2.0:protocol" , 
                "Response");
    
    call.registerTypeMapping( XACMLPolicyQueryType.class ,
                              xacmlPolicyQueryQName,
                              new SerializerFactory() ,
                              new DeserializerFactory() );

    call.registerTypeMapping( Response.class ,
                              responseQName,
                              new SerializerFactory() ,
                              new DeserializerFactory() );

    
    /* call the service */
    
    Response response = 
      (Response) call.invoke( new Object[] { xacmlPolicyQuery } );
    
    return response;
  }

}
