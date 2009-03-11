package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.PolicyTypeString;
import org.glite.authz.pap.common.xacml.utils.DescriptionTypeHelper;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.common.xacml.wizard.exceptions.PolicyWizardException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyWizard extends XACMLWizard {

    protected static final AttributeWizardType attributeWizardType = AttributeWizardType.ACTION;

    protected static final String VISIBILITY_PRIVATE_PREFIX = "private";
    protected static final String VISIBILITY_PUBLIC_PREFIX = "public";
    private static final Logger log = LoggerFactory.getLogger(PolicyWizard.class);

    protected final String actionValue;
    protected String description = null;
    protected boolean isPrivate = false;
    protected PolicyTypeString policy = null;

    protected String policyId = null;
    protected String policyIdUniqueNumber;
    protected String policyIdVisibilityPrefix;
    protected final List<RuleWizard> ruleWizardList = new LinkedList<RuleWizard>();
    protected final TargetWizard targetWizard;
    protected String version = null;

    public PolicyWizard(AttributeWizard attributeWizard) {

        if (attributeWizardType != attributeWizard.getAttributeWizardType()) {
            throw new UnsupportedPolicyException("Attribute not supported: " + attributeWizard.getId());
        }

        actionValue = attributeWizard.getValue();
        targetWizard = new TargetWizard(attributeWizard);

        policyId = generateId();
        version = "1";
    }

    public PolicyWizard(PolicyType policy) throws UnsupportedPolicyException {

        if (!(policy instanceof PolicyTypeString)) {
            throw new PolicyWizardException("Argument is not PolicyTypeString");
        }

        targetWizard = new TargetWizard(policy.getTarget());

        List<AttributeWizard> targetAttributeWizardList = targetWizard.getAttributeWizardList();
        validateTargetAttributewizardList(targetAttributeWizardList);

        policyId = policy.getPolicyId();
        decomposePolicyId(policy.getPolicyId());

        actionValue = targetAttributeWizardList.get(0).getValue();

        if (policy.getDescription() != null) {
            description = policy.getDescription().getValue();
        }

        try {
            version = policy.getVersion();
            new Integer(version);
        } catch (NumberFormatException e) {
            throw new UnsupportedPolicyException("Wrong version format", e);
        }

        for (RuleType rule : policy.getRules()) {
            ruleWizardList.add(new RuleWizard(rule));
        }

        this.policy = (PolicyTypeString) policy;
    }

    public static String generateId(String prefix) {
        String id = generateUUID();

        if (prefix == null) {
            return id;
        }

        if (prefix.length() == 0) {
            return id;
        }

        return prefix + "_" + generateUUID();
    }

    public static boolean isPrivate(String policyId) {
        String[] idComponents = policyId.split("_");

        if (idComponents.length != 3)
            return false;

        if (VISIBILITY_PRIVATE_PREFIX.equals(idComponents[0]))
            return true;

        return false;
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private static void validateTargetAttributewizardList(List<AttributeWizard> targetAttributeWizardList) {

        if (targetAttributeWizardList == null) {
            throw new UnsupportedPolicySetWizardException("targetAttributeWizardList is null");
        }

        if (targetAttributeWizardList.size() != 1) {
            throw new UnsupportedPolicySetWizardException("Only one resource attribute is supported (found "
                    + targetAttributeWizardList.size() + " attributes)");
        }

        AttributeWizard aw = targetAttributeWizardList.get(0);

        if (aw.getAttributeWizardType() != attributeWizardType) {
            throw new UnsupportedPolicySetWizardException("Only one action attribute is supported");
        }
    }

    public void addObligation(ObligationWizard obligationWizard) {
    // TODO: implement me
    	invalidatePolicyType();
    }

    public void addObligation(String obligationId, List<AttributeWizard> attributeWizardList) {
    // TODO: implement me
    	invalidatePolicyType();
    }

    public void addRule(AttributeWizard attribute, EffectType effect) {
        addRule(new RuleWizard(attribute, effect));
    }

    public void addRule(int index, AttributeWizard attribute, EffectType effect) {
        addRule(index, new RuleWizard(attribute, effect));
    }

    public void addRule(int index, List<AttributeWizard> targetAttributeList, EffectType effect) {
        addRule(new RuleWizard(targetAttributeList, effect));
    }

    public void addRule(int index, RuleWizard ruleWizard) {
        ruleWizardList.add(index, ruleWizard);
        invalidatePolicyType();
    }

    public void addRule(List<AttributeWizard> targetAttributeList, EffectType effect) {
        addRule(new RuleWizard(targetAttributeList, effect));
    }

    public void addRule(RuleWizard ruleWizard) {
        ruleWizardList.add(ruleWizard);
        invalidatePolicyType();
    }

    public boolean denyRuleForAttributeExists(AttributeWizard attributeWizard) {

        for (RuleWizard ruleWizard : ruleWizardList) {
            if (ruleWizard.deniesAttribute(attributeWizard)) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfRules() {
        return ruleWizardList.size();
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getPolicyIdPrefix() {
        return policyIdVisibilityPrefix;
    }

    public String getTagAndValue() {
        return String.format("%s \"%s\"", attributeWizardType.getId(), actionValue);
    }

    public int getVersion() {
        return Integer.valueOf(version);
    }

    public String getVersionString() {
        return version;
    }

    public PolicyType getXACML() {
        initPolicyTypeIfNotSet();
        return policy;
    }

    public void increaseVersion() {
        setVersion(getVersion() + 1);
    }

    public boolean isDOMReleased() {
        return (policy == null);
    }

    public boolean isEquivalent(PolicyType policy) {

        if (!(targetWizard.isEquivalent(policy.getTarget()))) {
            return false;
        }

        List<RuleType> ruleList = policy.getRules();

        if (ruleList.size() != ruleWizardList.size()) {
            return false;
        }

        for (int i = 0; i < ruleWizardList.size(); i++) {
            if (!(ruleWizardList.get(i).isEquivalent(ruleList.get(i)))) {
                return false;
            }
        }

        if (description != null) {
            if (!(description.equals(policy.getDescription()))) {
                return false;
            }
        } else {
            if (policy.getDescription() != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isPublic() {
        return !isPrivate;
    }

    public void releaseChildrenDOM() {
        targetWizard.releaseChildrenDOM();
        targetWizard.releaseDOM();
        for (RuleWizard ruleWizard : ruleWizardList) {
            ruleWizard.releaseChildrenDOM();
            ruleWizard.releaseDOM();
        }
    }

    public void releaseDOM() {
        if (policy != null) {
            policy.releaseDOM();
            policy = null;
        }
    }

    public boolean removeDenyRuleForAttribute(AttributeWizard attributeWizard) {

        for (int i = 0; i < ruleWizardList.size(); i++) {

            RuleWizard ruleWizard = ruleWizardList.get(i);

            if (ruleWizard.deniesAttribute(attributeWizard)) {

                ruleWizardList.remove(i);

                if (policy != null) {
                    policy.getRules().remove(i);
                }
                invalidatePolicyType();
                return true;
            }
        }
        return false;
    }

    public void setDescription(String value) {
        this.description = value;
        if (policy != null) {
            policy.setDescription(DescriptionTypeHelper.build(value));
        }
        invalidatePolicyType();
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
        if (policy != null) {
            policy.setPolicyId(policyId);
        }
        invalidatePolicyType();
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;

        setPolicyIdVisibilityPrefix(isPrivate);

        policyId = composeId();

        if (policy != null) {
            policy.setPolicyId(policyId);
        }
        invalidatePolicyType();
    }

    public void setVersion(int version) {
        this.version = Integer.toString(version);

        if (policy != null) {
            policy.setVersion(this.version);
        }
        invalidatePolicyType();
    }

    public String toFormattedString() {
        return toFormattedString(false);
    }

    public String toFormattedString(boolean printPolicyId) {
        return toFormattedString(0, 4, printPolicyId, false);
    }

    public String toFormattedString(int baseIndentation, int internalIndentation) {
        return toFormattedString(baseIndentation, internalIndentation, false, false);
    }

    public String toFormattedString(int baseIndentation, int internalIndentation, boolean printPolicyId, boolean printRuleIds) {

        String baseIndentString = Utils.fillWithSpaces(baseIndentation);
        String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
        StringBuffer sb = new StringBuffer();

        if (printPolicyId) {
            sb.append(String.format("%sid=%s\n", baseIndentString, policyId));
        }

        if (isPrivate()) {
            sb.append(String.format("%sprivate\n", baseIndentString));
        }

        sb.append(String.format("%saction \"%s\" {\n", baseIndentString, actionValue));

        if (description != null) {
            sb.append(String.format("%sdescription=\"%s\"\n", indentString, description));
        }

        for (RuleWizard ruleWizard : ruleWizardList) {
            sb.append(ruleWizard.toFormattedString(baseIndentation + internalIndentation, internalIndentation, printRuleIds));
            sb.append('\n');
        }

        sb.append(baseIndentString + "}");

        return sb.toString();
    }

    public String toXACMLString() {
        initPolicyTypeIfNotSet();
        return XMLObjectHelper.toString(policy);
    }

    private String composeId() {
        return policyIdVisibilityPrefix + "_" + policyIdUniqueNumber;
    }

    private void decomposePolicyId(String policyId) throws UnsupportedPolicyException {

        String[] idTokens = policyId.split("_");

        if (idTokens.length == 0) {
            throw new UnsupportedPolicyException("Unrecognized policyId: " + policyId);
        }

        isPrivate = false;
        int idUniqueNumberTokenIndex = -1;

        if (idTokens.length == 0) {
            idUniqueNumberTokenIndex = 0;
        }

        if (idTokens.length == 2) {

            idUniqueNumberTokenIndex = 1;

            if (VISIBILITY_PRIVATE_PREFIX.equals(idTokens[0])) {
                this.isPrivate = true;
            } else if (!(VISIBILITY_PUBLIC_PREFIX.equals(idTokens[0]))) {
                idUniqueNumberTokenIndex = -1;
            }
        }

        if (idUniqueNumberTokenIndex == -1) {
            throw new UnsupportedPolicyException("Unrecognized policyId: " + policyId);
        }

        policyIdUniqueNumber = idTokens[idUniqueNumberTokenIndex];
        setPolicyIdVisibilityPrefix(isPrivate);
    }

    private String generateId() {

        setPolicyIdVisibilityPrefix(isPrivate);

        policyIdUniqueNumber = generateUUID();

        return composeId();
    }

    private void initPolicyTypeIfNotSet() {
        if (policy == null) {
            log.debug("Initializing policyType");
            setPolicyType();
        } else {
            log.debug("policyType already initialized");
        }
    }

    private void invalidatePolicyType() {
    	releaseChildrenDOM();
    	releaseDOM();
    }

    private void setPolicyIdVisibilityPrefix(boolean isPrivate) {
        if (isPrivate) {
            policyIdVisibilityPrefix = VISIBILITY_PRIVATE_PREFIX;
        } else {
            policyIdVisibilityPrefix = VISIBILITY_PUBLIC_PREFIX;
        }
    }
    
    private void setPolicyType() {

        releaseDOM();

        policy = new PolicyTypeString(PolicyHelper.build(policyId, PolicyHelper.RULE_COMBALG_FIRST_APPLICABLE));

        if (description != null) {
            policy.setDescription(DescriptionTypeHelper.build(description));
        }

        policy.setTarget(targetWizard.getXACML());
        policy.setVersion(version);

        for (RuleWizard ruleWizard : ruleWizardList) {
            policy.getRules().add(ruleWizard.getXACML());
        }
    }
}
