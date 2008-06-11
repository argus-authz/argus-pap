package org.glite.authz.pap.common.xacml.impl;

import java.io.File;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.w3c.dom.Document;

public class PolicySetBuilderImpl implements PolicySetBuilder {
	
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

	public PolicySet buildFromDOM(Document doc) {
		return new PolicySetImpl(doc);
	}

	public PolicySet buildFromFile(File file) {
		return new PolicySetImpl(file);
	}

	public PolicySet buildFromFile(String fileName) {
		return new PolicySetImpl(fileName);
	}

}
