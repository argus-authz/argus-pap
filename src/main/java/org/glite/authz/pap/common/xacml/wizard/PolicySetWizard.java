package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;

public abstract class PolicySetWizard {
    
    protected enum InsertionType {
        WHOLE_OBJECT, AS_REFERENCE
    }

    protected static final InsertionType INSERTION_TYPE = InsertionType.AS_REFERENCE;

    protected final List<XACMLObject> policyList;
    protected final List<PolicySetWizard> policySetWizardList;
    protected final PolicySetType policySet;

    protected PolicySetWizard(String policySetId, String policyCombiningAlgorithmId, TargetType target,
            ObligationsType obligations) {

        policyList = new LinkedList<XACMLObject>();
        policySetWizardList = new LinkedList<PolicySetWizard>();

        if ((target == null) && (obligations == null))
            policySet = PolicySetHelper.buildWithAnyTarget(policySetId, policyCombiningAlgorithmId);
        else
            policySet = PolicySetHelper.build(policySetId, policyCombiningAlgorithmId, target,
                    obligations);

        policyList.add(policySet);
    }

    public void addPolicy(PolicyType policy) {
        if (INSERTION_TYPE == InsertionType.AS_REFERENCE) {
            addPolicyAsReference(policy);
        } else {
            addPolicyAsWholeObject(policy);
        }
    }

    public void addPolicy(PolicyWizard policyWiz) {
        addPolicy(policyWiz.getPolicyType());

    }

    public void addPolicySet(PolicySetWizard policySetWiz) {
        if (INSERTION_TYPE == InsertionType.AS_REFERENCE) {
            addPolicySetAsReference(policySetWiz);
        } else {
            addPolicySetAsWholeObject(policySetWiz);
        }
    }

    public PolicySetType getPolicySetType() {
        return policySet;
    }

    public List<XACMLObject> getPolicyTreeAsList() {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>(policyList);

        for (PolicySetWizard elem : policySetWizardList) {
            resultList.addAll(elem.getPolicyTreeAsList());
        }

        return resultList;
    }
    
    public String toString() {
        return PolicySetHelper.toString(policySet);
    }

    protected void addPolicyAsReference(PolicyType policy) {
        PolicySetHelper.addPolicyReference(policySet, policy.getPolicyId());
        policyList.add(policy);
    }

    protected void addPolicyAsReference(PolicyWizard policyWiz) {
        addPolicyAsReference(policyWiz.getPolicyType());
    }

    protected void addPolicyAsWholeObject(PolicyType policy) {
        PolicySetHelper.addPolicy(policySet, policy);
    }

    protected void addPolicyAsWholeObject(PolicyWizard policyWiz) {
        addPolicy(policyWiz.getPolicyType());

    }

    protected void addPolicySetAsReference(PolicySetWizard childPolicySetWizard) {
        PolicySetType childPolicySet = childPolicySetWizard.getPolicySetType();

        PolicySetHelper.addPolicySetReference(this.policySet, childPolicySet.getPolicySetId());

        policySetWizardList.add(childPolicySetWizard);
    }

    protected void addPolicySetAsWholeObject(PolicySetType policySet) {
        PolicySetHelper.addPolicySet(this.policySet, policySet);
    }

    protected void addPolicySetAsWholeObject(PolicySetWizard policySetWiz) {
        addPolicySetAsWholeObject(policySetWiz.getPolicySetType());
    }
}
