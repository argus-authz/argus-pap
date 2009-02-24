package org.glite.authz.pap.common.xacml.wizard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.Utils;
import org.glite.authz.pap.common.xacml.utils.DescriptionTypeHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.PolicySetWizardException;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;

public class PolicySetWizard {

    protected enum InsertionType {
        AS_REFERENCE, WHOLE_OBJECT
    }

    protected static final InsertionType INSERTION_TYPE = InsertionType.AS_REFERENCE;

    protected final TargetWizard targetWizard;
    protected final List<XACMLObject> policyList;
    protected final PolicySetType policySet;
    protected final String resourceValue;

    protected final List<PolicySetWizard> policySetWizardList;

//    public PolicySetWizard(List<AttributeWizard> targetAttributeWizardList) {
//        if (targetAttributeWizardList == null) {
//            targetAttributeWizardList = new LinkedList<AttributeWizard>();
//        }
//
//        validateTargetAttributewizardList(targetAttributeWizardList);
//
//        resourceValue = targetAttributeWizardList.get(0).getValue();
//
//        targetWizard = new TargetWizard(targetAttributeWizardList);
//
//        policySet = PolicySetHelper.build(WizardUtils.generateId(null), PolicySetHelper.COMB_ALG_FIRST_APPLICABLE, targetWizard
//                .getXACML(), null);
//
//        policyList = new LinkedList<XACMLObject>();
//        policySetWizardList = new LinkedList<PolicySetWizard>();
//
//    }
    
    public PolicySetWizard(AttributeWizard attributeWizard) {
        
        List<AttributeWizard> targetAttributeWizardList = new ArrayList<AttributeWizard>(1);
        targetAttributeWizardList.add(attributeWizard);

        validateTargetAttributewizardList(targetAttributeWizardList);

        resourceValue = targetAttributeWizardList.get(0).getValue();

        targetWizard = new TargetWizard(targetAttributeWizardList);

        policySet = PolicySetHelper.build(WizardUtils.generateId(null), PolicySetHelper.COMB_ALG_FIRST_APPLICABLE, targetWizard
                .getXACML(), null);

        policyList = new LinkedList<XACMLObject>();
        policySetWizardList = new LinkedList<PolicySetWizard>();

    }

    public PolicySetWizard(PolicySetType policySet, List<PolicyType> policyList, List<PolicySetType> childPolicySetList) {

        this.policySet = policySet;
        targetWizard = new TargetWizard(policySet.getTarget());
        validateTargetAttributewizardList(targetWizard.getAttributeWizardList());
        resourceValue = targetWizard.getAttributeWizardList().get(0).getValue();
        this.policyList = new LinkedList<XACMLObject>();
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
                    this.policyList.add(policy);
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

    @Deprecated
    protected PolicySetWizard(String policySetId, String policyCombiningAlgorithmId, TargetType target,
            ObligationsType obligations) {

        policyList = new LinkedList<XACMLObject>();
        policySetWizardList = new LinkedList<PolicySetWizard>();

        if ((target == null) && (obligations == null))
            policySet = PolicySetHelper.buildWithAnyTarget(policySetId, policyCombiningAlgorithmId);
        else
            policySet = PolicySetHelper.build(policySetId, policyCombiningAlgorithmId, target, obligations);

        targetWizard = null;
        resourceValue = null;

        policyList.add(policySet);
    }

    public void addObligation(String obligationId, List<AttributeWizard> attributeWizardList) {
    // TODO: implement me
    }

    public void addPolicy(PolicyType policy) {
        if (INSERTION_TYPE == InsertionType.AS_REFERENCE) {
            addPolicyAsReference(policy);
        } else {
            addPolicyAsWholeObject(policy);
        }
    }

    public void addPolicy(PolicyWizard policyWizard) {
        addPolicy(policyWizard.getXACML());
    }

    public void addPolicySet(PolicySetWizard policySetWizard) {
        if (INSERTION_TYPE == InsertionType.AS_REFERENCE) {
            addPolicySetAsReference(policySetWizard);
        } else {
            addPolicySetAsWholeObject(policySetWizard);
        }
    }

    public boolean targetEquals(PolicySetWizard policySetWizard) {
        return targetWizard.equals(policySetWizard.getTargetWizard());
    }

    public String getDescription() {
        DescriptionType dt = policySet.getDescription();

        if (dt == null) {
            return null;
        }

        return dt.getValue();
    }

    public List<XACMLObject> getPolicyTreeAsList() {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>(policyList);

        for (PolicySetWizard elem : policySetWizardList) {
            resultList.addAll(elem.getPolicyTreeAsList());
        }

        return resultList;
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

    public String toString() {
        return PolicySetHelper.toString(policySet);
    }

    public String toFormattedString(boolean printIds) {
        return toFormattedString(0, 4, printIds, false);
    }

    public String toFormattedString(int baseIndentation, int internalIndentation, boolean printIds, boolean printRuleIds) {

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

        for (XACMLObject xacmlObject : policyList) {

            if (!(xacmlObject instanceof PolicyType)) {
                continue;
            }

            PolicyType policy = (PolicyType) xacmlObject;

            PolicyWizard policyWizard = new PolicyWizard(policy);

            sb.append(policyWizard.toFormattedString(baseIndentation + internalIndentation, internalIndentation, printIds,
                    printRuleIds));

            sb.append('\n');

        }

        sb.append(baseIndentString + "}");

        return sb.toString();
    }

    protected void addPolicyAsReference(PolicyType policy) {
        PolicySetHelper.addPolicyReference(policySet, policy.getPolicyId());
        policyList.add(policy);
    }

    protected void addPolicyAsReference(PolicyWizard policyWiz) {
        addPolicyAsReference(policyWiz.getXACML());
    }

    protected void addPolicyAsWholeObject(PolicyType policy) {
        PolicySetHelper.addPolicy(policySet, policy);
    }

    protected void addPolicyAsWholeObject(PolicyWizard policyWiz) {
        addPolicy(policyWiz.getXACML());
    }

    protected void addPolicySetAsReference(PolicySetWizard childPolicySetWizard) {
        PolicySetHelper.addPolicySetReference(this.policySet, childPolicySetWizard.getXACML().getPolicySetId());
        policySetWizardList.add(childPolicySetWizard);
    }

    protected void addPolicySetAsWholeObject(PolicySetType policySet) {
        PolicySetHelper.addPolicySet(this.policySet, policySet);
    }

    protected void addPolicySetAsWholeObject(PolicySetWizard policySetWiz) {
        addPolicySetAsWholeObject(policySetWiz.getXACML());
    }

    private static void validateTargetAttributewizardList(List<AttributeWizard> targetAttributeWizardList) {

        if (targetAttributeWizardList.size() != 1) {
            throw new UnsupportedPolicySetWizardException("Wrong number of attributes, only one is supported");
        }

        AttributeWizard aw = targetAttributeWizardList.get(0);

        if (!aw.isResourceAttribute()) {
            throw new UnsupportedPolicySetWizardException("Only resource attributes are supported");
        }
    }
}
