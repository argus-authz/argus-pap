package org.glite.authz.pap.common.xacml;

public interface IdReference extends XACMLObject {
	
	public static enum Type {
		POLICYIDREFERENCE, POLICYSETIDREFERENCE;
	}

	public String getValue();

}