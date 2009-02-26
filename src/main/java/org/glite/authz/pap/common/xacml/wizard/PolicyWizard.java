package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.utils.DescriptionTypeHelper;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyWizard extends XACMLWizard {
	
	private static final Logger log = LoggerFactory.getLogger(PolicyWizard.class);

	private static final AttributeWizardType attributeWizardType = AttributeWizardType.ACTION;
	private static final String VISIBILITY_PRIVATE_PREFIX = "PRIVATE";
	private static final String VISIBILITY_PUBLIC_PREFIX = "PUBLIC";

	protected final String actionValue;
	protected final PolicyType policy;
	protected final TargetWizard targetWizard;

	private boolean isPrivate = false;
	private String policyIdUniqueNumber;
	private String policyIdVisibilityPrefix;

	public PolicyWizard(AttributeWizard attributeWizard) {

		if (attributeWizardType != attributeWizard.getAttributeWizardType()) {
			throw new UnsupportedPolicyException("Attribute not supported: " + attributeWizard.getId());
		}

		actionValue = attributeWizard.getValue();
		targetWizard = new TargetWizard(attributeWizard);
		policy = PolicyHelper.build(generateId(), PolicyHelper.RULE_COMBALG_FIRST_APPLICABLE);
		policy.setTarget(targetWizard.getXACML());
		
		setVersion(1);
	}

	public PolicyWizard(PolicyType policy) throws UnsupportedPolicyException {

		decomposePolicyId(policy.getPolicyId());

		targetWizard = new TargetWizard(policy.getTarget());
		List<AttributeWizard> targetAttributeWizardList = targetWizard.getAttributeWizardList();

		validateTargetAttributewizardList(targetAttributeWizardList);

		actionValue = targetAttributeWizardList.get(0).getValue();

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
		log.debug("Adding rule in position: " + index);
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

	public String getPolicyId() {
		return policy.getPolicyId();
	}

	public String getPolicyIdPrefix() {
		return policyIdVisibilityPrefix;
	}

	public String getTagAndValue() {
		return String.format("%s \"%s\"", attributeWizardType.getId(), actionValue);
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

		return false;
	}

	public boolean isBanPolicyForDN(String dn) {
		return isBanPolicy(dn, AttributeWizardType.DN);
	}

	public boolean isBanPolicyForFQAN(String dn) {
		return isBanPolicy(dn, AttributeWizardType.FQAN);
	}

	public boolean isPrivate() {
		return isPrivate;
	}
	
	public boolean isPublic() {
        return !isPrivate;
    }

	public void setDescription(String description) {
		policy.setDescription(DescriptionTypeHelper.build(description));
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;

		setPolicyIdVisibilityPrefix(isPrivate);

		policy.setPolicyId(composeId());
	}

	public void setVersion(int version) {
		policy.setVersion(Integer.toString(version));
	}
	
	public String toXACMLString() {
	    return XMLObjectHelper.toString(policy);
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

	public String toFormattedString(int baseIndentation, int internalIndentation, boolean printPolicyId,
			boolean printRuleIds) {

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

			sb.append(ruleWizard.toFormattedString(baseIndentation + internalIndentation,
				internalIndentation, printRuleIds));
			sb.append('\n');
		}

		sb.append(baseIndentString + "}");

		return sb.toString();
	}

	public String toString() {
		return PolicyHelper.toString(policy);
	}

	private String composeId() {
		return policyIdVisibilityPrefix + "_" + policyIdUniqueNumber;
	}

	private void decomposePolicyId(String policyId) throws UnsupportedPolicyException {

		String[] idComponents = policyId.split("_");

		if (idComponents.length != 2)
			throw new UnsupportedPolicyException("Unrecognized policyId: " + policyId);

		if (VISIBILITY_PRIVATE_PREFIX.equals(idComponents[0])) {
			this.isPrivate = true;
			setPolicyIdVisibilityPrefix(true);
		} else {
			this.isPrivate = false;
			setPolicyIdVisibilityPrefix(false);
		}

		policyIdUniqueNumber = idComponents[1];
	}

	private String fillwithSpaces(int n) {
		String s = "";

		for (int i = 0; i < n; i++)
			s += " ";

		return s;
	}

	private String generateId() {

		setPolicyIdVisibilityPrefix(isPrivate);

		policyIdUniqueNumber = generateUUID();

		return composeId();
	}

	private void setPolicyIdVisibilityPrefix(boolean isPrivate) {
		if (isPrivate)
			policyIdVisibilityPrefix = VISIBILITY_PRIVATE_PREFIX;
		else
			policyIdVisibilityPrefix = VISIBILITY_PUBLIC_PREFIX;
	}
}
