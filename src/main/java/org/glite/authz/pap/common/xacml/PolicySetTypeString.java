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
    private String policySetId = null;
    private String policySetString = null;

    public PolicySetTypeString(PolicySetType policySet) {
        this.policySet = policySet;
        policySetId = policySet.getPolicySetId();
    }

    public PolicySetTypeString(String policySetString) {
        this.policySetString = policySetString;
    }

    public PolicySetTypeString(String policySetId, String policySetString) {
        this(policySetString);
        this.policySetId = policySetId;
    }
    
    public void addNamespace(Namespace arg0) {
        initPolicySetTypeIfNotSet();
        policySet.addNamespace(arg0);
        invalidatePolicySetString();
    }

    @SuppressWarnings("unchecked")
    public void deregisterValidator(Validator arg0) {
        initPolicySetTypeIfNotSet();
        policySet.deregisterValidator(arg0);
        invalidatePolicySetString();
    }

    public void detach() {
        initPolicySetTypeIfNotSet();
        policySet.detach();
        invalidatePolicySetString();
    }

    public List<CombinerParametersType> getCombinerParameters() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getCombinerParameters();
    }

    public DescriptionType getDescription() {
        initPolicySetTypeIfNotSet();
        return policySet.getDescription();
    }

    public Element getDOM() {
        initPolicySetTypeIfNotSet();
        return policySet.getDOM();
    }

    public QName getElementQName() {
        initPolicySetTypeIfNotSet();
        return policySet.getElementQName();
    }

    public IDIndex getIDIndex() {
        initPolicySetTypeIfNotSet();
        return policySet.getIDIndex();
    }

    public Set<Namespace> getNamespaces() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getNamespaces();
    }

    public String getNoNamespaceSchemaLocation() {
        initPolicySetTypeIfNotSet();
        return policySet.getNoNamespaceSchemaLocation();
    }

    public ObligationsType getObligations() {
        initPolicySetTypeIfNotSet();
        return policySet.getObligations();
    }

    public List<XMLObject> getOrderedChildren() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getOrderedChildren();
    }

    public XMLObject getParent() {
        initPolicySetTypeIfNotSet();
        return policySet.getParent();
    }

    public List<PolicyType> getPolicies() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getPolicies();
    }

    public IndexedXMLObjectChildrenList<XACMLObject> getPolicyChoiceGroup() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getPolicyChoiceGroup();
    }

    public List<PolicyCombinerParametersType> getPolicyCombinerParameters() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getPolicyCombinerParameters();
    }

    public String getPolicyCombiningAlgoId() {
        initPolicySetTypeIfNotSet();
        return policySet.getPolicyCombiningAlgoId();
    }

    public List<IdReferenceType> getPolicyIdReferences() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getPolicyIdReferences();
    }

    public List<PolicySetCombinerParametersType> getPolicySetCombinerParameters() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getPolicySetCombinerParameters();
    }

    public DefaultsType getPolicySetDefaults() {
        initPolicySetTypeIfNotSet();
        return policySet.getPolicySetDefaults();
    }

    public String getPolicySetId() {
        if (policySetId == null) {
            log.trace("getPolicySetId(): PolicySetId is not set, need to build the DOM");
            initPolicySetTypeIfNotSet();
        }
        return policySetId;
    }

    public List<IdReferenceType> getPolicySetIdReferences() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getPolicySetIdReferences();
    }

    public List<PolicySetType> getPolicySets() {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        return policySet.getPolicySets();
    }

    public String getPolicySetString() {
        initPolicySetStringIfNotSet();
        return policySetString;
    }

    public String getSchemaLocation() {
        initPolicySetTypeIfNotSet();
        return policySet.getSchemaLocation();
    }

    public QName getSchemaType() {
        initPolicySetTypeIfNotSet();
        return policySet.getSchemaType();
    }

    public TargetType getTarget() {
        initPolicySetTypeIfNotSet();
        return policySet.getTarget();
    }

    @SuppressWarnings("unchecked")
    public List<Validator> getValidators() {
        initPolicySetTypeIfNotSet();
        return policySet.getValidators();
    }

    public String getVersion() {
        initPolicySetTypeIfNotSet();
        return policySet.getVersion();
    }

    public boolean hasChildren() {
        initPolicySetTypeIfNotSet();
        return policySet.hasChildren();
    }

    public boolean hasParent() {
        initPolicySetTypeIfNotSet();
        return policySet.hasParent();
    }

    public boolean isDOMLoaded() {
		return (policySet != null);
	}

    @SuppressWarnings("unchecked")
    public void registerValidator(Validator arg0) {
        initPolicySetTypeIfNotSet();
        policySet.registerValidator(arg0);
    }

    public void releaseChildrenDOM(boolean arg0) {
        releaseDOM();
    }

    public synchronized void releaseDOM() {
        if (policySet != null) {
            initPolicySetStringIfNotSet();
            log.trace("Invalidating policySetType");
            policySet.releaseChildrenDOM(true);
            policySet.releaseDOM();
            policySet = null;
        }
    }

    public void releaseParentDOM(boolean arg0) {
        if (policySet != null) {
            policySet.releaseParentDOM(arg0);
        }
    }

    public void removeNamespace(Namespace arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.removeNamespace(arg0);
    }

    public XMLObject resolveID(String arg0) {
        initPolicySetTypeIfNotSet();
        return policySet.resolveID(arg0);
    }

    public XMLObject resolveIDFromRoot(String arg0) {
        initPolicySetTypeIfNotSet();
        return policySet.resolveIDFromRoot(arg0);
    }

    public void setDescription(DescriptionType arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.setDescription(arg0);
    }

    public void setDOM(Element arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.setDOM(arg0);
    }

    public void setNoNamespaceSchemaLocation(String arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.setNoNamespaceSchemaLocation(arg0);
    }

    public void setObligations(ObligationsType arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.setObligations(arg0);
    }

    public void setParent(XMLObject arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.setParent(arg0);
    }

    public void setPolicyCombiningAlgoId(String arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.setPolicyCombiningAlgoId(arg0);
    }

    public void setPolicySetDefaults(DefaultsType arg0) {
        initPolicySetTypeIfNotSet();
        invalidatePolicySetString();
        policySet.setPolicySetDefaults(arg0);
    }

    public void setPolicySetId(String arg0) {
        initPolicySetTypeIfNotSet();
        policySet.setPolicySetId(arg0);
        policySetId = arg0;
        invalidatePolicySetString();
    }

    public void setPolicySetString(String policySetString) {
        this.policySetString = policySetString;
        invalidatePolicySetId();
        releaseDOM();
    }
    
    public void setPolicySetString(String policySetId, String policySetString) {
        this.policySetString = policySetString;
        this.policySetId = policySetId;
        invalidatePolicySetId();
        releaseDOM();
    }

    public void setSchemaLocation(String arg0) {
        initPolicySetTypeIfNotSet();
        policySet.setSchemaLocation(arg0);
        invalidatePolicySetString();
    }

    public void setTarget(TargetType arg0) {
        initPolicySetTypeIfNotSet();
        policySet.setTarget(arg0);
        invalidatePolicySetString();
    }

    public void setVersion(String arg0) {
        initPolicySetTypeIfNotSet();
        policySet.setVersion(arg0);
        invalidatePolicySetString();
    }

    public void validate(boolean arg0) throws ValidationException {
        initPolicySetTypeIfNotSet();
        policySet.validate(arg0);
    }

    private void initPolicySetStringIfNotSet() {
        if (policySetString == null) {
            log.trace("Initializing policySetString id=" + policySet.getPolicySetId());
            policySetString = PolicyHelper.toString(policySet);
            policySetId = policySet.getPolicySetId();
        } else {
//            log.debug("policySetString already initialized, skipping initialization step");
        }
    }

    private synchronized void initPolicySetTypeIfNotSet() {
        if (policySet == null) {
            policySet = PolicySetHelper.getInstance().buildFromString(policySetString);
            policySetId = policySet.getPolicySetId();
            log.trace("Initializing policySetType id=" + policySetId);
        } else {
//            log.debug("policySetType already initialized, skipping initialization step");
        }
    }

    private void invalidatePolicySetId() {
        log.trace("Invalidating policySetId");
        policySetId = null;
    }

    private void invalidatePolicySetString() {
        log.trace("Invalidating policySetString");
        policySetString = null;
    }
}
