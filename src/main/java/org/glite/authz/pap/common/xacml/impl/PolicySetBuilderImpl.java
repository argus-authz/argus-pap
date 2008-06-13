package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.w3c.dom.Document;

public class PolicySetBuilderImpl extends PolicySetBuilder {
	
private static PolicySetBuilderImpl instance = null;
	
	public static PolicySetBuilderImpl getInstance() {
		if (instance == null) {
			return new PolicySetBuilderImpl();
		} else {
			return instance;
		}
	}
	
	private PolicySetBuilderImpl() { } 

	public PolicySet build(String policySetId, String policyCombinerAlgorithmId) {
		// TODO Auto-generated method stub
		return null;
	}

	public PolicySet build(Document doc) {
		return new PolicySetImpl(doc);
	}
}
