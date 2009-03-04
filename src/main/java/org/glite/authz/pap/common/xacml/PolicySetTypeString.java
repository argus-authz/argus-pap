package org.glite.authz.pap.common.xacml;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.CombinerParametersType;
import org.opensaml.xacml.policy.DefaultsType;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicyCombinerParametersType;
import org.opensaml.xacml.policy.PolicySetCombinerParametersType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.IDIndex;
import org.opensaml.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;
import org.w3c.dom.Element;

public class PolicySetTypeString implements PolicySetType {
	
	PolicySetType policySet = null;
	String policySetString = null;
	
	public PolicySetTypeString(String policySetString) {
		this.policySetString = policySetString;
	}
	
	public void addNamespace(Namespace arg0) {
	// TODO Auto-generated method stub

	}
	
	@SuppressWarnings("unchecked")
	public void deregisterValidator(Validator arg0) {
	// TODO Auto-generated method stub

	}

	public void detach() {
	// TODO Auto-generated method stub

	}

	public List<CombinerParametersType> getCombinerParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public DescriptionType getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getDOM() {
		// TODO Auto-generated method stub
		return null;
	}

	public QName getElementQName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDIndex getIDIndex() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Namespace> getNamespaces() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNoNamespaceSchemaLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public ObligationsType getObligations() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<XMLObject> getOrderedChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public XMLObject getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PolicyType> getPolicies() {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedXMLObjectChildrenList<XACMLObject> getPolicyChoiceGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PolicyCombinerParametersType> getPolicyCombinerParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPolicyCombiningAlgoId() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IdReferenceType> getPolicyIdReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PolicySetCombinerParametersType> getPolicySetCombinerParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public DefaultsType getPolicySetDefaults() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPolicySetId() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IdReferenceType> getPolicySetIdReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PolicySetType> getPolicySets() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPolicySetString() {
		return policySetString;
	}

	public String getSchemaLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public QName getSchemaType() {
		// TODO Auto-generated method stub
		return null;
	}

	public TargetType getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Validator> getValidators() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	public void registerValidator(Validator arg0) {
	// TODO Auto-generated method stub

	}

	public void releaseChildrenDOM(boolean arg0) {
	// TODO Auto-generated method stub

	}

	public void releaseDOM() {
	// TODO Auto-generated method stub

	}

	public void releaseParentDOM(boolean arg0) {
	// TODO Auto-generated method stub

	}

	public void releasePolicySetType() {
		policySet = null;
	}

	public void removeNamespace(Namespace arg0) {
	// TODO Auto-generated method stub

	}

	public XMLObject resolveID(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public XMLObject resolveIDFromRoot(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDescription(DescriptionType arg0) {
	// TODO Auto-generated method stub

	}

	public void setDOM(Element arg0) {
	// TODO Auto-generated method stub

	}

	public void setNoNamespaceSchemaLocation(String arg0) {
	// TODO Auto-generated method stub

	}

	public void setObligations(ObligationsType arg0) {
	// TODO Auto-generated method stub

	}

	public void setParent(XMLObject arg0) {
	// TODO Auto-generated method stub

	}

	public void setPolicyCombiningAlgoId(String arg0) {
	// TODO Auto-generated method stub

	}

	public void setPolicySetDefaults(DefaultsType arg0) {
	// TODO Auto-generated method stub

	}

	public void setPolicySetId(String arg0) {
	// TODO Auto-generated method stub

	}

	public void setPolicySetString(String policySetString) {
		this.policySetString = policySetString;
	}

	public void setSchemaLocation(String arg0) {
	// TODO Auto-generated method stub

	}

	public void setTarget(TargetType arg0) {
	// TODO Auto-generated method stub

	}

	public void setVersion(String arg0) {
	// TODO Auto-generated method stub

	}

	public void validate(boolean arg0) throws ValidationException {
	// TODO Auto-generated method stub

	}

	private void init() {
		if (policySet == null) {
			policySet = PolicySetHelper.getInstance().buildFromString(policySetString);
		}
	}

}
