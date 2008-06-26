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
 * File : ProvisioningService.java
 *
 * Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */

package org.glite.authz.pap.provisioning;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.XACMLPolicyQueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisioningService {

  final Logger logger = LoggerFactory.getLogger( ProvisioningService.class );

  public Response XACMLPolicyQuery( XACMLPolicyQueryType query )
      throws java.rmi.RemoteException {

    if ( logger.isDebugEnabled() ) {
      logger.debug( "Received XACLMPolicyQuery " + ProvisioningServiceUtils.xmlObjectToString( query ) );
    }

    /* check a few things about the query */

    try {
      ProvisioningServiceUtils.checkQuery( query );
    } 
    catch ( VersionMismatchException e ) {
      logger.error( e.getMessage(), e );
      return ProvisioningServiceUtils.createResponse( query , e );
    } 
    catch ( MissingIssuerException e ) {
      logger.error( e.getMessage(), e );
      return ProvisioningServiceUtils.createResponse( query , e );
    } 
    catch ( WrongFormatIssuerException e ) {
      logger.error( e.getMessage(), e );
      return ProvisioningServiceUtils.createResponse( query , e );
    }

    /* get local policies */
    
    PAPContainer papContainer = RepositoryManager.getPAPManager().get( new PAP( "Local" ) );
    
    List<PolicySetType> policySetList = papContainer.getAllPolicySets();
    List<PolicyType> policyList = papContainer.getAllPolicies();
    
    /* prepare the response */

//    Response response = ProvisioningServiceUtils.createResponse( query , resultList );
    Response response = null;

    if ( logger.isDebugEnabled() ) {
      logger.debug( ProvisioningServiceUtils.xmlObjectToString( response ) );
    }

    return response;
  }

}
