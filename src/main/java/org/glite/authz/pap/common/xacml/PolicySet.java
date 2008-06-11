package org.glite.authz.pap.common.xacml;

import java.util.List;

public abstract class PolicySet extends AbstractPolicy {

	public static final String COMB_ALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";
	public static final String COMB_ALG_ORDERED_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-deny-overrides";
	public static final String COMB_ALG_ORDERED_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-permit-overrides";

	public abstract void deletePolicyReference(String policyId);

	public abstract void deletePolicySetReference(String policySetId);

	public abstract AbstractPolicy getFirstChildren();

	public abstract String getId();

	public abstract AbstractPolicy getLastChildren();

	public abstract int getNumberOfChildren();

	public abstract List<AbstractPolicy> getOrderedListOfChildren();

	public abstract void insertPolicyReferenceAsFirst(String value);
	
	public abstract void insertPolicyReferenceAsLast(String value);

	public abstract void insertPolicySetReferenceAsFirst(String value);
	
	public abstract void insertPolicySetReferenceAsLast(String value);

	public abstract boolean referenceIdExists(String id);

	public abstract boolean policySetReferenceIdExists(String id);

	public abstract boolean policyReferenceIdExists(String id);

	public abstract void setId(String policySetId);
	
	public abstract List<String> getPolicySetIdReferences();
	
}
