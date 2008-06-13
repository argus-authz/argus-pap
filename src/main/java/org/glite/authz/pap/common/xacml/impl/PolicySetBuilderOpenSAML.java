package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.w3c.dom.Document;

public class PolicySetBuilderOpenSAML extends PolicySetBuilder {
	
	private static PolicySetBuilderOpenSAML instance = null;
	
	public static PolicySetBuilderOpenSAML getInstance() {
		if (instance == null) {
			return new PolicySetBuilderOpenSAML();
		} else {
			return instance;
		}
	}
	
	private PolicySetBuilderOpenSAML() { } 

	public PolicySet build(String policySetId, String policyCombinerAlgorithmId) {
		return new PolicySetOpenSAML(policySetId, policyCombinerAlgorithmId);
	}

	public PolicySet build(Document doc) {
		return new PolicySetOpenSAML(doc.getDocumentElement());
	}

}
