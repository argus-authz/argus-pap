package org.glite.authz.pap.common.xacml;

public abstract class IdReference extends AbstractPolicy {
	
	public static enum Type {
		POLICYIDREFERENCE, POLICYSETIDREFERENCE;
	}

	public abstract String getValue();
	
	public abstract boolean isPolicyReference();
	
	public abstract boolean isPolicySetReference();

}