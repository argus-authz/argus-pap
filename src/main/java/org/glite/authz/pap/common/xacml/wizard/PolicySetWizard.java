package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.utils.DescriptionTypeHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.common.xacml.wizard.exceptions.PolicySetWizardException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicyException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class PolicySetWizard extends XACMLWizard {

    protected static final AttributeWizardType attributeWizardType = AttributeWizardType.RESOURCE_PS;
    protected String description = null;
    protected PolicySetType policySet = null;
    protected String policySetId = null;
    protected final List<PolicySetWizard> policySetWizardList;
    protected final List<PolicyWizard> policyWizardList;
    protected final String resourceValue;
    protected final TargetWizard targetWizard;
    protected String version = null;

    public PolicySetWizard(AttributeWizard attributeWizard) {

        if (attributeWizard.getAttributeWizardType() != attributeWizardType) {
            throw new UnsupportedPolicySetWizardException("Attribute not supported: " + attributeWizard.getId());
        }

        resourceValue = attributeWizard.getValue();
        policySetId = WizardUtils.generateId(null);
        version = "1";
        targetWizard = new TargetWizard(attributeWizard);
        policyWizardList = new LinkedList<PolicyWizard>();
        policySetWizardList = new LinkedList<PolicySetWizard>();
    }

    public PolicySetWizard(PolicySetType policySet, PolicyType[] policyList, PolicySetType[] childPolicySetList) {

        this.policySet = policySet;
        policySetId = policySet.getPolicySetId();
        targetWizard = new TargetWizard(policySet.getTarget());

        validateTargetAttributewizardList(targetWizard.getAttributeWizardList());

        resourceValue = targetWizard.getAttributeWizardList().get(0).getValue();

        try {
            version = policySet.getVersion();
            new Integer(version);
        } catch (NumberFormatException e) {
            throw new UnsupportedPolicyException(String.format("Wrong version format (policySetId=\"%s\")",
                                                               policySet.getPolicySetId()), e);
        }

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
                    PolicyWizard policyWizard = new PolicyWizard(policy);
                    policyWizardList.add(policyWizard);
                    policyWizard.releaseDOM();
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
            for (PolicySetType childPolicySet : childPolicySetList) {
                if (policySetIdReference.equals(childPolicySet.getPolicySetId())) {
                    PolicySetWizard psw = new PolicySetWizard(childPolicySet, policyList, childPolicySetList);
                    policySetWizardList.add(psw);
                    psw.releaseDOM();
                    found = true;
                }
            }
            if (!found) {
                throw new PolicySetWizardException("Not found policy set reference: " + policySetIdReference);
            }
        }
    }

    public static void increaseVersion(PolicySetType policySet) {
        int version;

        try {
            version = (new Integer(policySet.getVersion())).intValue();
            version++;
        } catch (NumberFormatException e) {
            version = 1;
        }

        policySet.setVersion(Integer.toString(version));
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
        // initPolicySetTypeIfNotSet();
        // PolicySetHelper.addPolicyReference(policySet, policyWizard.getPolicyId());
        policyWizardList.add(policyWizard);
    }

    public void addPolicySet(PolicySetWizard policySetWizard) {
        // initPolicySetTypeIfNotSet();
        // PolicySetHelper.addPolicySetReference(policySet, policySetWizard.getXACML().getPolicySetId());
        policySetWizardList.add(policySetWizard);
    }

    public String getDescription() {
        return description;
    }

    public String getPolicySetId() {
        return policySetId;
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

    public List<String> getPolicyIdReferences() {

        List<String> idRefList = new ArrayList<String>(policyWizardList.size());

        for (PolicyWizard policyWizard : policyWizardList) {
            idRefList.add(policyWizard.getPolicyId());
        }

        return idRefList;
    }

    public List<String> getPolicySetIdReferences() {

        List<String> idRefList = new ArrayList<String>(policySetWizardList.size());

        for (PolicySetWizard policySetWizard : policySetWizardList) {
            idRefList.add(policySetWizard.getPolicySetId());
        }

        return idRefList;
    }

    public int getVersion() {
        return Integer.valueOf(version);
    }

    public String getVersionString() {
        return version;
    }

    public PolicySetType getXACML() {
        initPolicySetTypeIfNotSet();
        return policySet;
    }

    public PolicySetType getXACMLNoReferences() {
        return buildXACMLNoReferences();
    }

    public void increaseVersion() {
        setVersion(getVersion() + 1);
    }

    public void releaseDOM() {
        if (policySet != null) {
            policySet.releaseDOM();
            policySet = null;
        }
    }

    public void setDescription(String value) {
        description = value;
        if (policySet != null) {
            policySet.setDescription(DescriptionTypeHelper.build(value));
        }
    }

    public void setPolicySetId(String id) {
        policySetId = id;
        if (policySet != null) {
            policySet.setPolicySetId(id);
        }
    }

    public void setVersion(int version) {
        this.version = Integer.toString(version);

        if (policySet != null) {
            policySet.setVersion(this.version);
        }
    }

    public boolean targetEquals(PolicySetWizard policySetWizard) {
        return targetWizard.equals(policySetWizard.getTargetWizard());
    }

    public String toFormattedString(boolean printIds) {
        return toFormattedString(0, 4, printIds, false);
    }

    public String toFormattedString(boolean printIds, boolean printRulesId) {
        return toFormattedString(0, 4, printIds, printRulesId);
    }

    public String toFormattedString(int baseIndentation, int internalIndentation, boolean printIds, boolean printRulesId) {

        String baseIndentString = Utils.fillWithSpaces(baseIndentation);
        String indentString = Utils.fillWithSpaces(baseIndentation + internalIndentation);
        StringBuffer sb = new StringBuffer();

        if (printIds) {
            sb.append(String.format("%sid=%s\n", baseIndentString, policySetId));
        }

        sb.append(String.format("%sresource \"%s\" {\n", baseIndentString, resourceValue));

        if (description != null) {
            sb.append(String.format("%sdescription=\"%s\"\n", indentString, description));
        }

        for (PolicyWizard policyWizard : policyWizardList) {

            sb.append(policyWizard.toFormattedString(baseIndentation + internalIndentation,
                                                     internalIndentation,
                                                     printIds,
                                                     printRulesId));
            sb.append('\n');

        }

        sb.append(baseIndentString + "}");

        return sb.toString();
    }

    public String toXACMLString() {
        initPolicySetTypeIfNotSet();
        return XMLObjectHelper.toString(policySet);
    }

    private void initPolicySetTypeIfNotSet() {
        if (policySet == null) {
            setPolicySetType(true);
        }
    }

    private void setPolicySetType(boolean includeReferences) {

        releaseDOM();
        
        policySet = buildXACMLNoReferences();

        if (includeReferences) {
            for (PolicySetWizard policySetWizard : policySetWizardList) {
                PolicySetHelper.addPolicySetReference(policySet, policySetWizard.getPolicySetId());
            }

            for (PolicyWizard policyWizard : policyWizardList) {
                PolicySetHelper.addPolicyReference(policySet, policyWizard.getPolicyId());
            }
        }
    }
    
    private PolicySetTypeString buildXACMLNoReferences() {
        PolicySetTypeString policySet = new PolicySetTypeString(PolicySetHelper.build(policySetId,
                                                                  PolicySetHelper.COMB_ALG_FIRST_APPLICABLE,
                                                                  targetWizard.getXACML(),
                                                                  null));
        if (description != null) {
            policySet.setDescription(DescriptionTypeHelper.build(description));
        }

        policySet.setVersion(version);
        
        return policySet;
    }
}
