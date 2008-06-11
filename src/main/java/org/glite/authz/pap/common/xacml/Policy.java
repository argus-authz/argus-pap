package org.glite.authz.pap.common.xacml;


public abstract class Policy extends AbstractPolicy {
	public static final String COMB_ALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
	public static final String COMB_ALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
	public static final String COMB_ALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

	public abstract void setId(String policyId);

	public abstract String getId();

}
