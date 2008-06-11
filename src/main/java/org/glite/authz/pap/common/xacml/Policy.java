package org.glite.authz.pap.common.xacml;

import java.io.File;

public interface Policy extends AbstractPolicy {
	static final String COMB_ALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
	static final String COMB_ALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
	static final String COMB_ALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

	public void setId(String policyId);

	public String getId();

	public void toFile(File file);

	public void toFile(String fileName);

}
