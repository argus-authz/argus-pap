package org.glite.authz.pap.common.xacml;

import org.glite.authz.pap.common.xacml.impl.PolicyBuilderOpenSAML;
import org.glite.authz.pap.common.xacml.impl.PolicySetBuilderOpenSAML;

public class XACMLBuilderFactory {
	
	private static XACMLBuilderFactory instance = null;
	
	public static XACMLBuilderFactory getInstance() {
		if (instance == null) {
			instance = new XACMLBuilderFactory();
		}
		return instance;
	}
	
	private XACMLBuilderFactory() {}
	
	public PolicySetBuilder getPolicySetBuilder() {
		return PolicySetBuilderOpenSAML.getInstance();
	}
	
	public PolicyBuilder getPolicyBuilder() {
		return PolicyBuilderOpenSAML.getInstance();
	}
	
}
