package org.glite.authz.pap.common.xacml;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class PolicySetTypeString implements PolicySetType {
	
    private static final Logger log = LoggerFactory.getLogger(PolicySetTypeString.class);
	private PolicySetType policySet = null;
	private String policySetString = null;
	
	public PolicySetTypeString(String policySetString) {
		this.policySetString = policySetString;
	}
	
	public void addNamespace(Namespace arg0) {
	    initPolicySetType();
	    policySet.addNamespace(arg0);
	    invalidatePolicySetString();
	}
	
	@SuppressWarnings("unchecked")
	public void deregisterValidator(Validator arg0) {
	    initPolicySetType();
        policySet.deregisterValidator(arg0);
        invalidatePolicySetString();
	}

	public void detach() {
	    initPolicySetType();
        policySet.detach();
        invalidatePolicySetString();
	}

	public List<CombinerParametersType> getCombinerParameters() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getCombinerParameters();
	}

	public DescriptionType getDescription() {
	    initPolicySetType();
        return policySet.getDescription();
	}

	public Element getDOM() {
	    initPolicySetType();
        return policySet.getDOM();
	}

	public QName getElementQName() {
	    initPolicySetType();
        return policySet.getElementQName();
	}

	public IDIndex getIDIndex() {
	    initPolicySetType();
        return policySet.getIDIndex();
	}

	public Set<Namespace> getNamespaces() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getNamespaces();
	}

	public String getNoNamespaceSchemaLocation() {
	    initPolicySetType();
        return policySet.getNoNamespaceSchemaLocation();
	}

	public ObligationsType getObligations() {
	    initPolicySetType();
        return policySet.getObligations();
	}

	public List<XMLObject> getOrderedChildren() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getOrderedChildren();
	}

	public XMLObject getParent() {
	    initPolicySetType();
        return policySet.getParent();
	}

	public List<PolicyType> getPolicies() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getPolicies();
	}

	public IndexedXMLObjectChildrenList<XACMLObject> getPolicyChoiceGroup() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getPolicyChoiceGroup();
	}

	public List<PolicyCombinerParametersType> getPolicyCombinerParameters() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getPolicyCombinerParameters();
	}

	public String getPolicyCombiningAlgoId() {
		initPolicySetType();
        return policySet.getPolicyCombiningAlgoId();
	}

	public List<IdReferenceType> getPolicyIdReferences() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getPolicyIdReferences();
	}

	public List<PolicySetCombinerParametersType> getPolicySetCombinerParameters() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getPolicySetCombinerParameters();
	}

	public DefaultsType getPolicySetDefaults() {
	    initPolicySetType();
        return policySet.getPolicySetDefaults();
	}

	public String getPolicySetId() {
	    initPolicySetType();
        return policySet.getPolicySetId();
	}

	public List<IdReferenceType> getPolicySetIdReferences() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getPolicySetIdReferences();
	}

	public List<PolicySetType> getPolicySets() {
	    initPolicySetType();
	    invalidatePolicySetString();
        return policySet.getPolicySets();
	}

	public String getPolicySetString() {
	    initPolicySetString();
		return policySetString;
	}

	public String getSchemaLocation() {
	    initPolicySetType();
        return policySet.getSchemaLocation();
	}

	public QName getSchemaType() {
	    initPolicySetType();
        return policySet.getSchemaType();
	}

	public TargetType getTarget() {
	    initPolicySetType();
        return policySet.getTarget();
	}

	@SuppressWarnings("unchecked")
	public List<Validator> getValidators() {
	    initPolicySetType();
        return policySet.getValidators();
	}

	public String getVersion() {
	    initPolicySetType();
        return policySet.getVersion();
	}

	public boolean hasChildren() {
	    initPolicySetType();
        return policySet.hasChildren();
	}

	public boolean hasParent() {
	    initPolicySetType();
        return policySet.hasParent();
	}

	@SuppressWarnings("unchecked")
	public void registerValidator(Validator arg0) {
	    initPolicySetType();
        policySet.registerValidator(arg0);
	}

	public void releaseChildrenDOM(boolean arg0) {
	    initPolicySetType();
        policySet.releaseChildrenDOM(arg0);
	}

	public void releaseDOM() {
	    if (policySet != null) {
	    	policySet.releaseDOM();
	    	invalidatePolicySetType();
	    }
	}

	public void releaseParentDOM(boolean arg0) {
	    initPolicySetType();
        policySet.releaseParentDOM(arg0);
	}

	public void removeNamespace(Namespace arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.removeNamespace(arg0);
	}

	public XMLObject resolveID(String arg0) {
	    initPolicySetType();
        return policySet.resolveID(arg0);
	}

	public XMLObject resolveIDFromRoot(String arg0) {
	    initPolicySetType();
        return policySet.resolveIDFromRoot(arg0);
	}

	public void setDescription(DescriptionType arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setDescription(arg0);
	}

	public void setDOM(Element arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setDOM(arg0);
	}

	public void setNoNamespaceSchemaLocation(String arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setNoNamespaceSchemaLocation(arg0);
	}

	public void setObligations(ObligationsType arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setObligations(arg0);
	}

	public void setParent(XMLObject arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setParent(arg0);
	}

	public void setPolicyCombiningAlgoId(String arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setPolicyCombiningAlgoId(arg0);
	}

	public void setPolicySetDefaults(DefaultsType arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setPolicySetDefaults(arg0);
	}

	public void setPolicySetId(String arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setPolicySetId(arg0);
	}

	public void setPolicySetString(String policySetString) {
	    invalidatePolicySetType();
		this.policySetString = policySetString;
	}

	public void setSchemaLocation(String arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setSchemaLocation(arg0);
	}

	public void setTarget(TargetType arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setTarget(arg0);
	}

	public void setVersion(String arg0) {
	    initPolicySetType();
	    invalidatePolicySetString();
        policySet.setVersion(arg0);
	}

	public void validate(boolean arg0) throws ValidationException {
	    initPolicySetType();
        policySet.validate(arg0);
	}

	private void invalidatePolicySetString() {
        log.debug("Invalidating policySetString");
        policySetString = null;
    }
    
    private void invalidatePolicySetType() {
        log.debug("Invalidating policySetType");
        policySet = null;
    }

    private void initPolicySetString() {
        if (policySetString == null) {
            log.debug("Initializing policySetString");
            policySetString = PolicyHelper.toString(policySet);
        } else {
            log.debug("policySetString already initialized, skipping initialization step");
        }
    }

    private void initPolicySetType() {
        if (policySet == null) {
            log.debug("Initializing policySetType");
            policySet = PolicySetHelper.getInstance().buildFromString(policySetString);
        } else {
            log.debug("policySetType already initialized, skipping initialization step");
        }
    }

}
