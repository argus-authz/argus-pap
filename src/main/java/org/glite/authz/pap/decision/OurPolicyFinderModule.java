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
 * File : OurPolicyFinder.java
 *
 * Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */

package org.glite.authz.pap.decision;

import java.util.List;

import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.repository.dao.ProvisioningServiceDAO;
import org.opensaml.xacml.XACMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.PolicySet;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;
import com.sun.xacml.support.finder.PolicyCollection;
import com.sun.xacml.support.finder.TopLevelPolicyException;

/**
 * @author Valerio Venturi <valerio.venturi@cnaf.infn.it>
 *
 */
public class OurPolicyFinderModule extends PolicyFinderModule {

    final private Logger logger = 
	LoggerFactory.getLogger(OurPolicyFinderModule.class);

    PolicyCollection policyCollection = new PolicyCollection();
    
    /* (non-Javadoc)
     * @see com.sun.xacml.finder.PolicyFinderModule#init(com.sun.xacml.finder.PolicyFinder)
     */
    @Override
    public void init(PolicyFinder arg0) {

	logger.debug("Initializing " + this.getClass().getName());
	
	// load the policies from the repository

	List<XACMLObject> policies = 
	    ProvisioningServiceDAO.getInstance().pdpQuery();
	
	// convert each to sunxacml and feed to the policy collection
	
	for(XACMLObject xacmlObject : policies) {

	    Node node = XMLObjectHelper.getDOM(xacmlObject);
		
	    PolicySet policy = null;
		
	    try {
		policy = PolicySet.getInstance(node);
	    } catch (ParsingException e) {
		// TODO should rollback to empty policies if a load is not done
		logger.error("Error parsing policy");
		break;
	    }

	    // add the policy to the collection
	    
	    if(!policyCollection.addPolicy(policy)) {
		// TODO should rollback to empty policies if a load is not done
		logger.error("Error adding policy to policy collection");
		break;
	    }
		
	}
	
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        
	AbstractPolicy policy = null;
	
	try {
	    policy = policyCollection.getPolicy(context);
	} catch (TopLevelPolicyException e) {
	    return new PolicyFinderResult(e.getStatus());
	}

	if (policy == null)
	    return new PolicyFinderResult();
	else
	    return new PolicyFinderResult(policy);
	
    }
    
}
