package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.utils.DescriptionTypeHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.common.xacml.wizard.exceptions.PolicySetWizardException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class PolicySetWizard extends XACMLWizard {

	protected static final AttributeWizardType attributeWizardType = AttributeWizardType.RESOURCE_PS;
	protected final PolicySetType policySet;
	protected final List<PolicySetWizard> policySetWizardList;
	protected final List<PolicyWizard> policyWizardList;
	protected final String resourceValue;

	protected final TargetWizard targetWizard;

	public PolicySetWizard(AttributeWizard attributeWizard) {

		if (attributeWizard.getAttributeWizardType() != attributeWizardType) {
			throw new UnsupportedPolicySetWizardException("Attribute not supported: "
					+ attributeWizard.getId());
		}

		resourceValue = attributeWizard.getValue();

		targetWizard = new TargetWizard(attributeWizard);

		policySet = PolicySetHelper.build(WizardUtils.generateId(null),
			PolicySetHelper.COMB_ALG_FIRST_APPLICABLE, targetWizard.getXACML(), null);

		policyWizardList = new LinkedList<PolicyWizard>();
		policySetWizardList = new LinkedList<PolicySetWizard>();

	}

	public PolicySetWizard(PolicySetType policySet, PolicyType[] policyList,
			PolicySetType[] childPolicySetList) {

		this.policySet = policySet;
		targetWizard = new TargetWizard(policySet.getTarget());
		
		validateTargetAttributewizardList(targetWizard.getAttributeWizardList());
		
		resourceValue = targetWizard.getAttributeWizardList().get(0).getValue();
		policyWizardList = new LinkedList<PolicyWizard>();
		policySetWizardList = new LinkedList<PolicySetWizard>();

		// add referenced policies
		List<String> idReferenceList = PolicySetHelper.getPolicyIdReferencesValues(policySet);

		if (idReferenceList.size() > 0) {
			if (policyList == null) {
				throw new PolicySetWizardException("policyList is null");
			}
		}

		for (String policyIdReference : idReferenceList) {
			
			boolean found = false;
			
			for (PolicyType policy : policyList) {
				if (policyIdReference.equals(policy.getPolicyId())) {
					policyWizardList.add(new PolicyWizard(policy));
					found = true;
				}
			}
			
			if (!found) {
				throw new PolicySetWizardException("Not found policy reference: " + policyIdReference);
			}
		}

		// add referenced policy sets
		idReferenceList = PolicySetHelper.getPolicySetIdReferencesValues(policySet);

		if (idReferenceList.size() > 0) {
			if (childPolicySetList == null) {
				throw new PolicySetWizardException("childPolicySetList is null");
			}
		}

		for (String policySetIdReference : idReferenceList) {
			boolean found = false;
			for (PolicySetType ps : childPolicySetList) {
				if (policySetIdReference.equals(ps.getPolicySetId())) {
					PolicySetWizard psw = new PolicySetWizard(ps, policyList, childPolicySetList);
					policySetWizardList.add(psw);
					found = true;
				}
			}
			if (!found) {
				throw new PolicySetWizardException("Not found policy set reference: " + policySetIdReference);
			}
		}

	}

	private static void validateTargetAttributewizardList(List<AttributeWizard> targetAttributeWizardList) {

		if (targetAttributeWizardList.size() != 1) {
			throw new UnsupportedPolicySetWizardException("Wrong number of attributes, only one is supported");
		}

		AttributeWizard aw = targetAttributeWizardList.get(0);

		if (aw.getAttributeWizardType() != attributeWizardType) {
			throw new UnsupportedPolicySetWizardException("Only resource attributes are supported");
		}
	}

	public void addObligation(ObligationWizard obligationWizard) {
	// TODO: implement me
	}

	public void addObligation(String obligationId, List<AttributeWizard> attributeWizardList) {
	// TODO: implement me
	}

	public void addPolicy(PolicyWizard policyWizard) {
		PolicySetHelper.addPolicyReference(policySet, policyWizard.getPolicyId());
		policyWizardList.add(policyWizard);
	}

	public void addPolicySet(PolicySetWizard policySetWizard) {
		PolicySetHelper.addPolicySetReference(policySet, policySetWizard.getXACML().getPolicySetId());
		policySetWizardList.add(policySetWizard);
	}

	public String getDescription() {
		DescriptionType dt = policySet.getDescription();

		if (dt == null) {
			return null;
		}

		return dt.getValue();
	}

	public String getPolicySetId() {
		return policySet.getPolicySetId();
	}

	public List<PolicyWizard> getPolicyWizardList() {
		return policyWizardList;
	}

	public String getTagAndValue() {
		return String.format("%s \"%s\"", attributeWizardType.getId(), resourceValue);
	}

	public TargetWizard getTargetWizard() {
		return targetWizard;
	}

	public PolicySetType getXACML() {
		return policySet;
	}

	public void setDescription(String value) {
		policySet.setDescription(DescriptionTypeHelper.build(value));
	}

	public boolean targetEquals(PolicySetWizard policySetWizard) {
		return targetWizard.equals(policySetWizard.getTargetWizard());
	}

	public String toFormattedString(boolean printIds) {
		return toFormattedString(0, 4, printIds, false);
	}

	public String toFormattedString(int baseIndentation, int internalIndentation, boolean printIds,
			boolean printRuleIds) {

		String baseIndentString = Utils.fillWithSpaces(baseIndentation);
		String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
		StringBuffer sb = new StringBuffer();

		if (printIds) {
			sb.append(String.format("%sid=%s\n", baseIndentString, policySet.getPolicySetId()));
		}

		sb.append(String.format("%sresource \"%s\" {\n", baseIndentString, resourceValue));

		String description = getDescription();

		if (description != null) {
			sb.append(String.format("%sdescription=\"%s\"\n", indentString, description));
		}

		for (PolicyWizard policyWizard : policyWizardList) {

			sb.append(policyWizard.toFormattedString(baseIndentation + internalIndentation,
				internalIndentation, printIds, printRuleIds));

			sb.append('\n');

		}

		sb.append(baseIndentString + "}");

		return sb.toString();
	}

	public String toString() {
		return PolicySetHelper.toString(policySet);
	}
}
