package org.glite.authz.pap.common.xacml.impl;

import java.io.File;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.w3c.dom.Document;

public class OpenSAMLPolicySetBuilder implements PolicySetBuilder {
	private BasicParserPool ppMgr;
	private UnmarshallerFactory unmarshallerFactory;
	
	public static OpenSAMLPolicySetBuilder getInstance() {
		return new OpenSAMLPolicySetBuilder();
	}
	
	private OpenSAMLPolicySetBuilder() {
		ppMgr = new BasicParserPool();
		ppMgr.setNamespaceAware(true);
		unmarshallerFactory = Configuration.getUnmarshallerFactory();
		
	}

	public PolicySet buildFromDOM(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public PolicySet buildFromFile(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	public PolicySet buildFromFile(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
