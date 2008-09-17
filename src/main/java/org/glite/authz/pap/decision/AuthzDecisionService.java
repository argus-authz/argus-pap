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


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.glite.authz.pap.provisioning.ProvisioningServiceUtils;
import org.opensaml.saml2.core.Response;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(AuthzDecisionService.class);

    public Response XACMLAuthzDecisionQuery(XACMLAuthzDecisionQueryType query)
	    throws java.rmi.RemoteException {
	
	logger.debug(ProvisioningServiceUtils.xmlObjectToString(query));
	
	RequestType opensamlRequest = query.getRequest();
	
	// convert the opensaml request a sunxacml request

	RequestCtx sunxacmlRequest = null;
	
	try {
	    sunxacmlRequest = AuthzDecisionServiceUtils.toRequestCtx(opensamlRequest);
	} catch (MarshallingException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	} catch (ParsingException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	}
	
	// configure the policy evaluation engine
	
	logger.debug("Configuring policy decision point");
	
	// policy finder module
	
	PolicyFinderModule policyFinderModule = new OurPolicyFinderModule();
	
	Set<PolicyFinderModule> policyFinderModules = new HashSet<PolicyFinderModule>();
	policyFinderModules.add(policyFinderModule);
	
	PolicyFinder policyFinder = new PolicyFinder();
	policyFinder.setModules(policyFinderModules);
	
	// attribute finder from environment
	
	CurrentEnvModule envModule = new CurrentEnvModule();
	
	List<AttributeFinderModule> attributeFinderModules = 
	    new ArrayList<AttributeFinderModule>();
	attributeFinderModules.add(envModule);
	
	AttributeFinder attributeFinder = new AttributeFinder();
	attributeFinder.setModules(attributeFinderModules);
	
	// 
	
	PDPConfig pdpConfig = 
	    new PDPConfig(attributeFinder, policyFinder, null, null);
	
	PDP pdp = new PDP(pdpConfig);
	
	// call the policy evaluation engine
	
	logger.debug("Calling policy decision point");
	
	ResponseCtx sunxacmlResponse = pdp.evaluate(sunxacmlRequest);
	
	/* now back to openSAML */

	ResponseType opensamlXacmlResponse = null;
	
	try {
	    opensamlXacmlResponse = AuthzDecisionServiceUtils.toRequestType(sunxacmlResponse);
	} catch (UnsupportedEncodingException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	} catch (XMLParserException e) {
	    return AuthzDecisionServiceUtils.createResponse(query, e);
	}
	
	/*  create the response */
	
	Response response = 
	    AuthzDecisionServiceUtils.createResponse(query, opensamlXacmlResponse); 
		
	logger.debug("Sending " + ProvisioningServiceUtils.xmlObjectToString(response));
	
	return response; 
    }
    
}
