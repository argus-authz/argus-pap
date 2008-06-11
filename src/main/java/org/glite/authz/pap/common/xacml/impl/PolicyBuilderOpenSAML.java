package org.glite.authz.pap.common.xacml.impl;

import java.io.File;

import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicyBuilder;
import org.w3c.dom.Document;

public class PolicyBuilderOpenSAML implements PolicyBuilder {
	
private static PolicyBuilderOpenSAML instance = null;
	
	public static PolicyBuilderOpenSAML getInstance() {
		if (instance == null) {
			return new PolicyBuilderOpenSAML();
		} else {
			return instance;
		}
	}
	
	private PolicyBuilderOpenSAML() { } 

	public Policy build(String policyId, String ruleCombinerAlgorithmId) {
		return new PolicyOpenSAML(policyId, ruleCombinerAlgorithmId);
	}

	public Policy buildFromDOM(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy buildFromFile(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy buildFromFile(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
