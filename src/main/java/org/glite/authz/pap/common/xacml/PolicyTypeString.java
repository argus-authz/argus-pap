package org.glite.authz.pap.common.xacml;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.opensaml.xacml.policy.CombinerParametersType;
import org.opensaml.xacml.policy.DefaultsType;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleCombinerParametersType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xacml.policy.VariableDefinitionType;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.IDIndex;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;
import org.w3c.dom.Element;

public class PolicyTypeString implements PolicyType {

	private PolicyType policy = null;
	private String policyString = null;

	public PolicyTypeString(String policyString) {
		this.policyString = policyString;
	}

	public void addNamespace(Namespace arg0) {
		init();
		policy.addNamespace(arg0);
	}

	@SuppressWarnings("unchecked")
	public void deregisterValidator(Validator arg0) {
		init();
		policy.deregisterValidator(arg0);
	}

	public void detach() {
		init();
		policy.detach();
	}

	public List<CombinerParametersType> getCombinerParameters() {
		init();
		return policy.getCombinerParameters();
	}

	public DescriptionType getDescription() {
		init();
		return policy.getDescription();
	}

	public Element getDOM() {
		init();
		return policy.getDOM();
	}

	public QName getElementQName() {
		init();
		return policy.getElementQName();
	}

	public IDIndex getIDIndex() {
		init();
		return policy.getIDIndex();
	}

	public Set<Namespace> getNamespaces() {
		init();
		return policy.getNamespaces();
	}

	public String getNoNamespaceSchemaLocation() {
		init();
		return policy.getNoNamespaceSchemaLocation();
	}

	public ObligationsType getObligations() {
		init();
		return policy.getObligations();
	}

	public List<XMLObject> getOrderedChildren() {
		init();
		return policy.getOrderedChildren();
	}

	public XMLObject getParent() {
		init();
		return policy.getParent();
	}

	public DefaultsType getPolicyDefaults() {
		init();
		return policy.getPolicyDefaults();
	}

	public String getPolicyId() {
		init();
		return policy.getPolicyId();
	}

	public String getPolicyString() {
		return policyString;
	}

	public List<RuleCombinerParametersType> getRuleCombinerParameters() {
		init();
		return policy.getRuleCombinerParameters();
	}

	public String getRuleCombiningAlgoId() {
		init();
		return policy.getRuleCombiningAlgoId();
	}

	public List<RuleType> getRules() {
		init();
		return policy.getRules();
	}

	public String getSchemaLocation() {
		init();
		return policy.getSchemaLocation();
	}

	public QName getSchemaType() {
		init();
		return policy.getSchemaType();
	}

	public TargetType getTarget() {
		init();
		return policy.getTarget();
	}

	@SuppressWarnings("unchecked")
	public List<Validator> getValidators() {
		init();
		return policy.getValidators();
	}

	public List<VariableDefinitionType> getVariableDefinitions() {
		init();
		return policy.getVariableDefinitions();
	}

	public String getVersion() {
		init();
		return policy.getVersion();
	}

	public boolean hasChildren() {
		init();
		return policy.hasChildren();
	}

	public boolean hasParent() {
		init();
		return policy.hasParent();
	}

	@SuppressWarnings("unchecked")
	public void registerValidator(Validator arg0) {
		init();
		policy.registerValidator(arg0);
	}

	public void releaseChildrenDOM(boolean arg0) {
		init();
		policy.releaseChildrenDOM(arg0);
	}

	public void releaseDOM() {
		init();
		policy.releaseDOM();
	}

	public void releaseParentDOM(boolean arg0) {
		init();
		policy.releaseParentDOM(arg0);
	}

	public void releasePolicyType() {
		policy = null;
	}
	
	public void removeNamespace(Namespace arg0) {
		init();
		policy.removeNamespace(arg0);
	}

	public XMLObject resolveID(String arg0) {
		init();
		return policy.resolveID(arg0);
	}

	public XMLObject resolveIDFromRoot(String arg0) {
		init();
		return policy.resolveIDFromRoot(arg0);
	}

	public void setDescription(DescriptionType arg0) {
		init();
		policy.setDescription(arg0);
	}

	public void setDOM(Element arg0) {
		init();
		policy.setDOM(arg0);
	}

	public void setNoNamespaceSchemaLocation(String arg0) {
		init();
		policy.setNoNamespaceSchemaLocation(arg0);
	}

	public void setObligations(ObligationsType arg0) {
		init();
		policy.setObligations(arg0);
	}

	public void setParent(XMLObject arg0) {
		init();
		policy.setParent(arg0);
	}

	public void setPolicyDefaults(DefaultsType arg0) {
		init();
		policy.setPolicyDefaults(arg0);
	}

	public void setPolicyId(String arg0) {
		init();
		policy.setPolicyId(arg0);
	}

	public void setPolicyString(String policyString) {
		this.policyString = policyString;
	}

	public void setRuleCombiningAlgoId(String arg0) {
		init();
		policy.setRuleCombiningAlgoId(arg0);
	}

	public void setSchemaLocation(String arg0) {
		init();
		policy.setSchemaLocation(arg0);
	}

	public void setTarget(TargetType arg0) {
		init();
		policy.setTarget(arg0);
	}

	public void setVersion(String arg0) {
		init();
		policy.setVersion(arg0);
	}

	public void validate(boolean arg0) throws ValidationException {
		init();
		policy.validate(arg0);
	}

	private void init() {
		if (policy == null) {
			policy = PolicyHelper.getInstance().buildFromString(policyString);
		}
	}

}
