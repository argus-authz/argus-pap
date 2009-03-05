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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class PolicyTypeString implements PolicyType {

    private static final Logger log = LoggerFactory.getLogger(PolicyTypeString.class);
    private PolicyType policy = null;
    private String policyString = null;
    private String policyId = null;

    public PolicyTypeString(String policyString) {
        this.policyString = policyString;
    }

    public PolicyTypeString(String policyId, String policyString) {
        this.policyId = policyId;
        this.policyString = policyString;
    }

    public void addNamespace(Namespace arg0) {
        initPolicyType();
        policy.addNamespace(arg0);
        invalidatePolicyString();
    }

    @SuppressWarnings("unchecked")
    public void deregisterValidator(Validator arg0) {
        initPolicyType();
        policy.deregisterValidator(arg0);
        invalidatePolicyString();
    }

    public void detach() {
        initPolicyType();
        policy.detach();
        invalidatePolicyString();
    }

    public List<CombinerParametersType> getCombinerParameters() {
        initPolicyType();
        invalidatePolicyString();
        return policy.getCombinerParameters();
    }

    public DescriptionType getDescription() {
        initPolicyType();
        return policy.getDescription();
    }

    public Element getDOM() {
        initPolicyType();
        return policy.getDOM();
    }

    public QName getElementQName() {
        initPolicyType();
        return policy.getElementQName();
    }

    public IDIndex getIDIndex() {
        initPolicyType();
        return policy.getIDIndex();
    }

    public Set<Namespace> getNamespaces() {
        initPolicyType();
        invalidatePolicyString();
        return policy.getNamespaces();
    }

    public String getNoNamespaceSchemaLocation() {
        initPolicyType();
        return policy.getNoNamespaceSchemaLocation();
    }

    public ObligationsType getObligations() {
        initPolicyType();
        return policy.getObligations();
    }

    public List<XMLObject> getOrderedChildren() {
        initPolicyType();
        invalidatePolicyString();
        return policy.getOrderedChildren();
    }

    public XMLObject getParent() {
        initPolicyType();
        return policy.getParent();
    }

    public DefaultsType getPolicyDefaults() {
        initPolicyType();
        return policy.getPolicyDefaults();
    }

    public String getPolicyId() {
        if (policyId == null) {
            initPolicyType();
        }
        return policyId;
    }

    public String getPolicyString() {
        initPolicyString();
        return policyString;
    }

    public List<RuleCombinerParametersType> getRuleCombinerParameters() {
        initPolicyType();
        invalidatePolicyString();
        return policy.getRuleCombinerParameters();
    }

    public String getRuleCombiningAlgoId() {
        initPolicyType();
        return policy.getRuleCombiningAlgoId();
    }

    public List<RuleType> getRules() {
        initPolicyType();
        invalidatePolicyString();
        return policy.getRules();
    }

    public String getSchemaLocation() {
        initPolicyType();
        return policy.getSchemaLocation();
    }

    public QName getSchemaType() {
        initPolicyType();
        return policy.getSchemaType();
    }

    public TargetType getTarget() {
        initPolicyType();
        return policy.getTarget();
    }

    @SuppressWarnings("unchecked")
    public List<Validator> getValidators() {
        initPolicyType();
        invalidatePolicyString();
        return policy.getValidators();
    }

    public List<VariableDefinitionType> getVariableDefinitions() {
        initPolicyType();
        invalidatePolicyString();
        return policy.getVariableDefinitions();
    }

    public String getVersion() {
        initPolicyType();
        return policy.getVersion();
    }

    public boolean hasChildren() {
        initPolicyType();
        return policy.hasChildren();
    }

    public boolean hasParent() {
        initPolicyType();
        return policy.hasParent();
    }

    @SuppressWarnings("unchecked")
    public void registerValidator(Validator arg0) {
        initPolicyType();
        policy.registerValidator(arg0);
        invalidatePolicyString();
    }

    public void releaseChildrenDOM(boolean arg0) {
        initPolicyType();
        policy.releaseChildrenDOM(arg0);
    }

    public void releaseDOM() {
        if (policy!= null) {
        	policy.releaseDOM();
        	invalidatePolicyType();
        }
    }

    public void releaseParentDOM(boolean arg0) {
        initPolicyType();
        policy.releaseParentDOM(arg0);
    }

    public void removeNamespace(Namespace arg0) {
        initPolicyType();
        policy.removeNamespace(arg0);
        invalidatePolicyString();
    }

    public XMLObject resolveID(String arg0) {
        initPolicyType();
        return policy.resolveID(arg0);
    }

    public XMLObject resolveIDFromRoot(String arg0) {
        initPolicyType();
        return policy.resolveIDFromRoot(arg0);
    }

    public void setDescription(DescriptionType arg0) {
        initPolicyType();
        policy.setDescription(arg0);
        invalidatePolicyString();
    }

    public void setDOM(Element arg0) {
        initPolicyType();
        policy.setDOM(arg0);
        policyId = policy.getPolicyId();
        invalidatePolicyString();
    }

    public void setNoNamespaceSchemaLocation(String arg0) {
        initPolicyType();
        policy.setNoNamespaceSchemaLocation(arg0);
        invalidatePolicyString();
    }

    public void setObligations(ObligationsType arg0) {
        initPolicyType();
        policy.setObligations(arg0);
        invalidatePolicyString();
    }

    public void setParent(XMLObject arg0) {
        initPolicyType();
        policy.setParent(arg0);
        invalidatePolicyString();
    }

    public void setPolicyDefaults(DefaultsType arg0) {
        initPolicyType();
        policy.setPolicyDefaults(arg0);
        invalidatePolicyString();
    }

    public void setPolicyId(String arg0) {
        initPolicyType();
        policy.setPolicyId(arg0);
        policyId = policy.getPolicyId();
        invalidatePolicyString();
    }

    public void setPolicyString(String policyString) {
        this.policyString = policyString;
        invalidatePolicyId();
        invalidatePolicyType();
    }
    
    public void setPolicyString(String policyId, String policyString) {
        this.policyString = policyString;
        this.policyId = policyId;
        invalidatePolicyType();
    }

    public void setRuleCombiningAlgoId(String arg0) {
        initPolicyType();
        policy.setRuleCombiningAlgoId(arg0);
        invalidatePolicyString();
    }

    public void setSchemaLocation(String arg0) {
        initPolicyType();
        policy.setSchemaLocation(arg0);
        invalidatePolicyString();
    }

    public void setTarget(TargetType arg0) {
        initPolicyType();
        policy.setTarget(arg0);
        invalidatePolicyString();
    }

    public void setVersion(String arg0) {
        initPolicyType();
        policy.setVersion(arg0);
        invalidatePolicyString();
    }

    public void validate(boolean arg0) throws ValidationException {
        initPolicyType();
        policy.validate(arg0);
    }

    private void initPolicyString() {
        if (policyString == null) {
            log.debug("Initializing policyString, stacktrace: ", (new Throwable()));
            policyString = PolicyHelper.toString(policy);
        } else {
            log.debug("policyString already initialized, skipping initialization step");
        }
    }

    private void initPolicyType() {
        if (policy == null) {
            log.debug("Initializing policyType, stacktrace: ", (new Throwable()));
            policy = PolicyHelper.getInstance().buildFromString(policyString);
            policyId = policy.getPolicyId();
        } else {
            log.debug("policyType already initialized, skipping initialization step");
        }
    }

    private void invalidatePolicyString() {
        log.debug("Invalidating policyString");
        policyString = null;
    }

    private void invalidatePolicyId() {
        log.debug("Invalidating policyId");
        policyId = null;
    }

    private void invalidatePolicyType() {
        log.debug("Invalidating policyType");
        policy = null;
    }
}
