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

 File : ProvisioningServicePortType.java

 Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.provisioning.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;

public interface ProvisioningServicePortType {

  public Response xacmlPolicyQuery( XACMLPolicyQueryType xacmlPolicyQueryType )
      throws ServiceException, RemoteException;

}
