package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.utils.DescriptionTypeHelper;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;

public class PolicyWizard {

    private enum PolicyWizardType {
        BLACKLIST, SERVICECLASS
    }

    private static final String ALLOW_KEYWORD = "allow";
    private static final String BLACKLIST_POLICY_ID_PREFIX = "BlacklistPolicy";
    private static final String DENY_KEYWORD = "deny";
    private static final String EXCEPT_KEYWORD = "    except";
    private static final String SERVICECLASS_POLICY_ID_PREFIX = "ServiceClassPolicy";
    private static final String VISIBILITY_PRIVATE_PREFIX = "PRIVATE";
    private static final String VISIBILITY_PUBLIC_PREFIX = "PUBLIC";

    private String description = null; // to remove
    private boolean isPrivate = false;
    private List<List<AttributeWizard>> orExceptionsAttributeWizardList = null; // to
    // remove
    private String policyIdPrefix;
    private String policyIdUniqueNumber;
    private String policyIdVisibilityPrefix;
    private PolicyWizardType policyWizardType;
    private final List<AttributeWizard> targetAttributeWizardList;

    protected final String actionValue;
    protected final PolicyType policy;
    protected final TargetWizard targetWizard;

    public PolicyWizard(AttributeWizard attributeWizard) {

        List<AttributeWizard> targetAttributeWizardList = new ArrayList<AttributeWizard>(1);
        targetAttributeWizardList.add(attributeWizard);

        validateTargetAttributewizardList(targetAttributeWizardList);

        actionValue = targetAttributeWizardList.get(0).getValue();
        targetWizard = new TargetWizard(targetAttributeWizardList);
        this.targetAttributeWizardList = targetAttributeWizardList;

        policyWizardType = getPolicyWizardType(targetAttributeWizardList);

        policy = PolicyHelper.build(generateId(), PolicyHelper.RULE_COMBALG_FIRST_APPLICABLE);

        TargetWizard targetWizard = new TargetWizard(targetAttributeWizardList);

        policy.setTarget(targetWizard.getXACML());
        setVersion(1);
    }

    public PolicyWizard(PolicyType policy) throws UnsupportedPolicyException {

        decomposePolicyId(policy.getPolicyId());

        targetWizard = new TargetWizard(policy.getTarget());
        targetAttributeWizardList = targetWizard.getAttributeWizardList();

        if (targetAttributeWizardList.size() == 0) {
            targetAttributeWizardList.add(new AttributeWizard("action", "*"));
        }

        validateTargetAttributewizardList(targetAttributeWizardList);

        actionValue = targetAttributeWizardList.get(0).getValue();

        policyWizardType = getPolicyWizardType(targetAttributeWizardList);

        if (policy.getDescription() != null) {
            description = policy.getDescription().getValue();
        }

        try {
            new Integer(policy.getVersion());
        } catch (NumberFormatException e) {
            throw new UnsupportedPolicyException("Wrong version format", e);
        }

        this.policy = policy;
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

    private static PolicyWizardType getPolicyWizardType(List<AttributeWizard> targetAttributeWizardList) {
        AttributeWizardType serviceClassWizardType = AttributeWizardType.SERVICE_CLASS;

        for (AttributeWizard attribute : targetAttributeWizardList) {

            if (serviceClassWizardType.equals(attribute.getAttributeWizardType()))
                return PolicyWizardType.SERVICECLASS;
        }

        return PolicyWizardType.BLACKLIST;
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

        if (!aw.isActionAttribute()) {
            throw new UnsupportedPolicySetWizardException("Only one action attribute is supported");
        }
    }

    public void addObligation(ObligationWizard obligationWizard) {
    // TODO: implement me
    }

    public void addObligation(String obligationId, List<AttributeWizard> attributeWizardList) {
    // TODO: implement me
    }

    public void addRule(AttributeWizard attribute, EffectType effect) {
        List<AttributeWizard> targetAttributeList = new ArrayList<AttributeWizard>(1);
        targetAttributeList.add(attribute);
        RuleWizard ruleWizard = new RuleWizard(targetAttributeList, effect);
        policy.getRules().add(ruleWizard.getXACML());
    }

    public void addRule(int index, AttributeWizard attribute, EffectType effect) {
        List<AttributeWizard> targetAttributeList = new ArrayList<AttributeWizard>(1);
        targetAttributeList.add(attribute);
        RuleWizard ruleWizard = new RuleWizard(targetAttributeList, effect);
        policy.getRules().add(index, ruleWizard.getXACML());
    }

    public void addRule(int index, List<AttributeWizard> targetAttributeList, EffectType effect) {
        RuleWizard ruleWizard = new RuleWizard(targetAttributeList, effect);
        policy.getRules().add(index, ruleWizard.getXACML());
    }

    public void addRule(List<AttributeWizard> targetAttributeList, EffectType effect) {
        RuleWizard ruleWizard = new RuleWizard(targetAttributeList, effect);
        policy.getRules().add(ruleWizard.getXACML());
    }

    public void addRule(RuleWizard ruleWizard) {
        policy.getRules().add(ruleWizard.getXACML());
    }

    public String getDescription() {
        DescriptionType dt = policy.getDescription();

        if (dt == null) {
            return null;
        }

        return dt.getValue();
    }

    public List<List<AttributeWizard>> getOrExceptionsAttributeWizardList() {
        return orExceptionsAttributeWizardList;
    }

    public String getPolicyIdPrefix() {
        return policyIdVisibilityPrefix + "_" + policyIdPrefix;
    }

    public List<AttributeWizard> getTargetAttributeWizardList() {
        return targetAttributeWizardList;
    }

    public int getVersion() {
        return Integer.valueOf(policy.getVersion());
    }

    public PolicyType getXACML() {
        return policy;
    }

    public void increaseVersion() {
        setVersion(getVersion() + 1);
    }

    public boolean isBanPolicy(String attributeValue, AttributeWizardType bannedAttribute) {

        if (isServiceClassPolicy())
            return false;

        if (targetAttributeWizardList.size() != 2)
            return false;

        if (orExceptionsAttributeWizardList.size() != 0)
            return false;

        for (AttributeWizard attribute : targetAttributeWizardList) {

            if (AttributeWizardType.RESOURCE_PS.equals(attribute.getAttributeWizardType())) {
                if (!("*".equals(attribute.getValue())))
                    return false;
            } else if (bannedAttribute.equals(attribute.getAttributeWizardType())) {
                if (!(attribute.getValue().equals(attributeValue)))
                    return false;
            } else
                return false;
        }

        return true;
    }

    public boolean isBanPolicyForDN(String dn) {
        return isBanPolicy(dn, AttributeWizardType.DN);
    }

    public boolean isBanPolicyForFQAN(String dn) {
        return isBanPolicy(dn, AttributeWizardType.FQAN);
    }

    public boolean isBlacklistPolicy() {

        if (policyWizardType.equals(PolicyWizardType.BLACKLIST))
            return true;

        return false;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isServiceClassPolicy() {

        if (policyWizardType.equals(PolicyWizardType.SERVICECLASS))
            return true;

        return false;
    }

    public void setDescription(String description) {
        policy.setDescription(DescriptionTypeHelper.build(description));
        this.description = description;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;

        setPolicyIdVisibilityPrefix(isPrivate);

        policy.setPolicyId(composeId());
    }

    public void setVersion(int version) {
        policy.setVersion(Integer.toString(version));
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

        String baseIndentString = fillwithSpaces(baseIndentation);
        String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
        StringBuffer sb = new StringBuffer();

        if (printPolicyId) {
            sb.append(String.format("%sid=%s\n", baseIndentString, policy.getPolicyId()));
        }

        if (isPrivate()) {
            sb.append(String.format("%sprivate\n", baseIndentString));
        }

        sb.append(String.format("%saction \"%s\" {\n", baseIndentString, actionValue));

        String description = getDescription();

        if (description != null) {
            sb.append(String.format("%sdescription=\"%s\"\n", indentString, description));
        }

        List<RuleType> rules = policy.getRules();

        for (RuleType rule : rules) {
            RuleWizard ruleWizard = new RuleWizard(rule);

            sb.append(ruleWizard.toFormattedString(baseIndentation + internalIndentation, internalIndentation, printRuleIds));
            sb.append('\n');
        }

        sb.append(baseIndentString + "}");

        return sb.toString();
    }

    public String toNormalizedFormattedString(int policyIndent, boolean noId) {
        return toNormalizedFormattedString(policyIndent, 4, noId);
    }

    public String toNormalizedFormattedString(int policyIndent, int attributeIndent, boolean noId) {

        String policyIndentString = fillwithSpaces(policyIndent);

        String formattedString;

        if (!noId)
            formattedString = policyIndentString + "id=" + policy.getPolicyId() + "\n";
        else
            formattedString = new String();

        String effectIndentString = fillwithSpaces(policyIndent + attributeIndent);

        if (isPrivate())
            formattedString += effectIndentString + "private\n";

        if (description != null)
            formattedString += effectIndentString + "description \"" + description + "\"\n";

        String effectString;

        if (EffectType.Deny.equals(policy.getRules().get(0).getEffect()))
            effectString = DENY_KEYWORD + " ";
        else
            effectString = ALLOW_KEYWORD + " ";

        String attributeIndentString = fillwithSpaces(policyIndent + attributeIndent + effectString.length());
        formattedString += effectIndentString + effectString;

        for (int i = 0; i < targetAttributeWizardList.size(); i++) {

            AttributeWizard attributeWizard = targetAttributeWizardList.get(i);
            AttributeWizardType awt = attributeWizard.getAttributeWizardType();

            if (AttributeWizardType.RESOURCE_PS.equals(awt) || AttributeWizardType.SERVICE_CLASS.equals(awt))
                continue;

            if (i > 0)
                formattedString += attributeIndentString;

            formattedString += targetAttributeWizardList.get(i).toFormattedString() + "\n";

        }

        String exceptKeyString = EXCEPT_KEYWORD + " ";
        attributeIndentString = fillwithSpaces(policyIndent + attributeIndent + exceptKeyString.length());

        for (List<AttributeWizard> andList : orExceptionsAttributeWizardList) {
            if (andList.isEmpty())
                continue;

            formattedString += effectIndentString + exceptKeyString + andList.get(0).toFormattedString() + "\n";

            for (int i = 1; i < andList.size(); i++) {
                formattedString += attributeIndentString + andList.get(i).toFormattedString() + "\n";
            }
        }

        return formattedString;
    }

    public String toString() {
        return PolicyHelper.toString(policy);
    }

    private String composeId() {
        return policyIdVisibilityPrefix + "_" + policyIdPrefix + "_" + policyIdUniqueNumber;
    }

    private void decomposePolicyId(String policyId) throws UnsupportedPolicyException {

        String[] idComponents = policyId.split("_");

        if (idComponents.length != 3)
            throw new UnsupportedPolicyException("Unrecognized policyId: " + policyId);

        if (VISIBILITY_PRIVATE_PREFIX.equals(idComponents[0])) {
            this.isPrivate = true;
            setPolicyIdVisibilityPrefix(true);
        } else {
            this.isPrivate = false;
            setPolicyIdVisibilityPrefix(false);
        }

        if (BLACKLIST_POLICY_ID_PREFIX.equals(idComponents[1]))
            setPolicyIdPrefix(PolicyWizardType.BLACKLIST);
        else
            setPolicyIdPrefix(PolicyWizardType.SERVICECLASS);

        policyIdUniqueNumber = idComponents[2];

    }

    private String fillwithSpaces(int n) {
        String s = "";

        for (int i = 0; i < n; i++)
            s += " ";

        return s;
    }

    private String generateId() {

        setPolicyIdVisibilityPrefix(isPrivate);

        setPolicyIdPrefix(policyWizardType);

        policyIdUniqueNumber = generateUUID();

        return composeId();
    }

    private void setPolicyIdPrefix(PolicyWizardType wizardType) {
        if (wizardType.equals(PolicyWizardType.BLACKLIST))
            policyIdPrefix = BLACKLIST_POLICY_ID_PREFIX;
        else
            policyIdPrefix = SERVICECLASS_POLICY_ID_PREFIX;
    }

    private void setPolicyIdVisibilityPrefix(boolean isPrivate) {
        if (isPrivate)
            policyIdVisibilityPrefix = VISIBILITY_PRIVATE_PREFIX;
        else
            policyIdVisibilityPrefix = VISIBILITY_PUBLIC_PREFIX;
    }

}
