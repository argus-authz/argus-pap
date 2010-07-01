/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.common.xacml.impl;

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

/**
 * This class provides an implementation for the {@link PolicyType} interface. The implementation,
 * which is based on the one provided by OpenSAML, adds the facility to store the <i>Policy</i> as a
 * string in order to cut on memory usage.
 * <p>
 * The following methods does not need to build the DOM: {@link #getPolicyId()},
 * {@link #getPolicyString()}.<br>
 * All the other methods build the DOM object.<br>
 * In order to release the memory an holding just the <i>string</i> and the <i>id</i> use the
 * utility method {@link TypeStringUtils#releaseUnneededMemory(Object)}.
 * 
 */
public class PolicyTypeString implements PolicyType {

    private static final Logger log = LoggerFactory.getLogger(PolicyTypeString.class);
    private PolicyType policy = null;
    private String policyId = null;
    private String policyString = null;

    public PolicyTypeString(PolicyType policy) {
        this.policy = policy;
        policyId = policy.getPolicyId();
        policyString = null;
    }

    public PolicyTypeString(String policyString) {
        this.policyString = policyString;
    }

    public PolicyTypeString(String policyId, String policyString) {
        this.policyId = policyId;
        this.policyString = policyString;
    }

    public void addNamespace(Namespace arg0) {
        initPolicyTypeIfNotSet();
        policy.addNamespace(arg0);
        invalidatePolicyString();
    }

    @SuppressWarnings("unchecked")
    public void deregisterValidator(Validator arg0) {
        initPolicyTypeIfNotSet();
        policy.deregisterValidator(arg0);
        invalidatePolicyString();
    }

    public void detach() {
        initPolicyTypeIfNotSet();
        policy.detach();
        invalidatePolicyString();
    }

    public List<CombinerParametersType> getCombinerParameters() {
        initPolicyTypeIfNotSet();
        invalidatePolicyString();
        return policy.getCombinerParameters();
    }

    public DescriptionType getDescription() {
        initPolicyTypeIfNotSet();
        return policy.getDescription();
    }

    public Element getDOM() {
        initPolicyTypeIfNotSet();
        return policy.getDOM();
    }

    public QName getElementQName() {
        initPolicyTypeIfNotSet();
        return policy.getElementQName();
    }

    public IDIndex getIDIndex() {
        initPolicyTypeIfNotSet();
        return policy.getIDIndex();
    }

    public Set<Namespace> getNamespaces() {
        initPolicyTypeIfNotSet();
        invalidatePolicyString();
        return policy.getNamespaces();
    }

    public String getNoNamespaceSchemaLocation() {
        initPolicyTypeIfNotSet();
        return policy.getNoNamespaceSchemaLocation();
    }

    public ObligationsType getObligations() {
        initPolicyTypeIfNotSet();
        return policy.getObligations();
    }

    public List<XMLObject> getOrderedChildren() {
        initPolicyTypeIfNotSet();
        invalidatePolicyString();
        return policy.getOrderedChildren();
    }

    public XMLObject getParent() {
        initPolicyTypeIfNotSet();
        return policy.getParent();
    }

    public DefaultsType getPolicyDefaults() {
        initPolicyTypeIfNotSet();
        return policy.getPolicyDefaults();
    }

    public String getPolicyId() {
        if (policyId == null) {
            initPolicyTypeIfNotSet();
        }
        log.trace("get policyId: " + policyId);
        return policyId;
    }

    public String getPolicyString() {
        initPolicyStringIfNotSet();
        return policyString;
    }

    public List<RuleCombinerParametersType> getRuleCombinerParameters() {
        initPolicyTypeIfNotSet();
        invalidatePolicyString();
        return policy.getRuleCombinerParameters();
    }

    public String getRuleCombiningAlgoId() {
        initPolicyTypeIfNotSet();
        return policy.getRuleCombiningAlgoId();
    }

    public List<RuleType> getRules() {
        initPolicyTypeIfNotSet();
        invalidatePolicyString();
        return policy.getRules();
    }

    public String getSchemaLocation() {
        initPolicyTypeIfNotSet();
        return policy.getSchemaLocation();
    }

    public QName getSchemaType() {
        initPolicyTypeIfNotSet();
        return policy.getSchemaType();
    }

    public TargetType getTarget() {
        initPolicyTypeIfNotSet();
        return policy.getTarget();
    }

    @SuppressWarnings("unchecked")
    public List<Validator> getValidators() {
        initPolicyTypeIfNotSet();
        invalidatePolicyString();
        return policy.getValidators();
    }

    public List<VariableDefinitionType> getVariableDefinitions() {
        initPolicyTypeIfNotSet();
        invalidatePolicyString();
        return policy.getVariableDefinitions();
    }

    public String getVersion() {
        initPolicyTypeIfNotSet();
        return policy.getVersion();
    }

    public boolean hasChildren() {
        initPolicyTypeIfNotSet();
        return policy.hasChildren();
    }

    public boolean hasParent() {
        initPolicyTypeIfNotSet();
        return policy.hasParent();
    }

    public boolean isDOMLoaded() {
        return (policy != null);
    }

    @SuppressWarnings("unchecked")
    public void registerValidator(Validator arg0) {
        initPolicyTypeIfNotSet();
        policy.registerValidator(arg0);
        invalidatePolicyString();
    }

    public void releaseChildrenDOM(boolean arg0) {
        releaseDOM();
    }

    public synchronized void releaseDOM() {
        if (policy != null) {
            initPolicyStringIfNotSet();
            log.trace("Invalidating policyType");
            policy.releaseChildrenDOM(true);
            policy.releaseDOM();
            policy = null;
        }
    }

    public void releaseParentDOM(boolean arg0) {
        if (policy != null) {
            policy.releaseParentDOM(arg0);
        }
    }

    public void removeNamespace(Namespace arg0) {
        initPolicyTypeIfNotSet();
        policy.removeNamespace(arg0);
        invalidatePolicyString();
    }

    public XMLObject resolveID(String arg0) {
        initPolicyTypeIfNotSet();
        return policy.resolveID(arg0);
    }

    public XMLObject resolveIDFromRoot(String arg0) {
        initPolicyTypeIfNotSet();
        return policy.resolveIDFromRoot(arg0);
    }

    public void setDescription(DescriptionType arg0) {
        initPolicyTypeIfNotSet();
        policy.setDescription(arg0);
        invalidatePolicyString();
    }

    public void setDOM(Element arg0) {
        initPolicyTypeIfNotSet();
        policy.setDOM(arg0);
        policyId = policy.getPolicyId();
        invalidatePolicyString();
    }

    public void setNoNamespaceSchemaLocation(String arg0) {
        initPolicyTypeIfNotSet();
        policy.setNoNamespaceSchemaLocation(arg0);
        invalidatePolicyString();
    }

    public void setObligations(ObligationsType arg0) {
        initPolicyTypeIfNotSet();
        policy.setObligations(arg0);
        invalidatePolicyString();
    }

    public void setParent(XMLObject arg0) {
        initPolicyTypeIfNotSet();
        policy.setParent(arg0);
        invalidatePolicyString();
    }

    public void setPolicyDefaults(DefaultsType arg0) {
        initPolicyTypeIfNotSet();
        policy.setPolicyDefaults(arg0);
        invalidatePolicyString();
    }

    public void setPolicyId(String arg0) {
        log.trace("Setting policyId");
        initPolicyTypeIfNotSet();
        policyId = arg0;
        policy.setPolicyId(arg0);
        invalidatePolicyString();
    }

    public void setPolicyString(String policyString) {
        this.policyString = policyString;
        invalidatePolicyId();
        releaseDOM();
    }

    public void setPolicyString(String policyId, String policyString) {
        this.policyString = policyString;
        this.policyId = policyId;
        releaseDOM();
    }

    public void setRuleCombiningAlgoId(String arg0) {
        initPolicyTypeIfNotSet();
        policy.setRuleCombiningAlgoId(arg0);
        invalidatePolicyString();
    }

    public void setSchemaLocation(String arg0) {
        initPolicyTypeIfNotSet();
        policy.setSchemaLocation(arg0);
        invalidatePolicyString();
    }

    public void setTarget(TargetType arg0) {
        initPolicyTypeIfNotSet();
        policy.setTarget(arg0);
        invalidatePolicyString();
    }

    public void setVersion(String arg0) {
        initPolicyTypeIfNotSet();
        policy.setVersion(arg0);
        invalidatePolicyString();
    }

    public void validate(boolean arg0) throws ValidationException {
        initPolicyTypeIfNotSet();
        policy.validate(arg0);
    }

    private void initPolicyStringIfNotSet() {
        if (policyString == null) {
            log.trace("Initializing policyString");
            policyString = PolicyHelper.toString(policy);
            policyId = policy.getPolicyId();
        } else {
            // log.debug("policyString already initialized, skipping initialization step");
        }
    }

    private synchronized void initPolicyTypeIfNotSet() {
        if (policy == null) {
            log.trace("Initializing policyType");
            policy = PolicyHelper.getInstance().buildFromString(policyString);
            policyId = policy.getPolicyId();
        } else {
            // log.debug("policyType already initialized, skipping initialization step");
        }
    }

    private void invalidatePolicyId() {
        log.trace("Invalidating policyId");
        policyId = null;
    }

    private void invalidatePolicyString() {
        log.trace("Invalidating policyString");
        policyString = null;
    }
}
