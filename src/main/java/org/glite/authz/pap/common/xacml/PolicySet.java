package org.glite.authz.pap.common.xacml;

import java.io.File;
import java.util.List;

import org.glite.authz.pap.common.xacml.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;

public interface PolicySet extends XACMLObject {
	public void deletePolicyReference(String policyId);
	public void deletePolicySetReference(String policySetId);
	public XACMLObject getFirstXACMLObjectChildren();
	public String getId();
	public XACMLObject getLastXACMLObjectChildren();
	public int getNumberOfXACMLObjectChildren();
	public List<XACMLObject> getOrderedListOfXACMLObjectChildren();
	public void insertPolicyReferenceAsFirst(String value);
	public void insertPolicySetReferenceAsFirst(String value);
	/**
	 * @param file
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException
	 */
	public void printXACMLDOMToFile(File file);
	/**
	 * @param fileName
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException
	 */
	public void printXACMLDOMToFile(String fileName);
	public boolean referenceIdExists(String id);
	public boolean policySetReferenceIdExists(String id);
	public boolean policyReferenceIdExists(String id);
	public void setId(String policySetId);
}
