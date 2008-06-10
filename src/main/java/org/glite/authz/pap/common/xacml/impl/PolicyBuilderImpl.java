package org.glite.authz.pap.common.xacml.impl;

import java.io.File;

import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicyBuilder;
import org.w3c.dom.Document;

public class PolicyBuilderImpl implements PolicyBuilder {
	
private static PolicyBuilderImpl instance = null;
	
	public static PolicyBuilderImpl getInstance() {
		if (instance == null) {
			return new PolicyBuilderImpl();
		} else {
			return instance;
		}
	}
	
	private PolicyBuilderImpl() { } 

	public Policy buildFromDOM(Document doc) {
		return new PolicyImpl(doc);
	}

	public Policy buildFromFile(File file) {
		return new PolicyImpl(file);
	}

	public Policy buildFromFile(String fileName) {
		return new PolicyImpl(fileName);
	}

}
