package org.glite.authz.pap.common.xacml;

import java.io.File;
import java.util.List;

import org.glite.authz.pap.common.xacml.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;

public interface PolicySet extends AbstractPolicy {

	static final String COMB_ALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";
	static final String COMB_ALG_ORDERED_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-deny-overrides";
	static final String COMB_ALG_ORDERED_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-permit-overrides";

	public void deletePolicyReference(String policyId);

	public void deletePolicySetReference(String policySetId);

	public AbstractPolicy getFirstChildren();

	public String getId();

	public AbstractPolicy getLastChildren();

	public int getNumberOfChildren();

	public List<AbstractPolicy> getOrderedListOfChildren();

	public void insertPolicyReferenceAsFirst(String value);
	
	public void insertPolicyReferenceAsLast(String value);

	public void insertPolicySetReferenceAsFirst(String value);
	
	public void insertPolicySetReferenceAsLast(String value);

	/**
	 * @param file
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException
	 */
	public void toFile(File file);

	/**
	 * @param fileName
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException
	 */
	public void toFile(String fileName);

	public boolean referenceIdExists(String id);

	public boolean policySetReferenceIdExists(String id);

	public boolean policyReferenceIdExists(String id);

	public void setId(String policySetId);
}
