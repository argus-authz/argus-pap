package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class PolicySetWizard2 {
    
    protected enum InsertionType {
        AS_REFERENCE, WHOLE_OBJECT
    }

    protected static final InsertionType INSERTION_TYPE = InsertionType.AS_REFERENCE;

    private final TargetWizard targetWizard;
    protected final List<XACMLObject> policyList;
    protected final PolicySetType policySet;
    
    protected final List<PolicySetWizard2> policySetWizardList;
    
    public PolicySetWizard2(List<AttributeWizard> targetAttributeWizardList) {
        if (targetAttributeWizardList == null) {
            targetAttributeWizardList = new LinkedList<AttributeWizard>();
        }
        
        targetWizard = new TargetWizard(targetAttributeWizardList);
        
        policySet = PolicySetHelper.build(WizardUtils.generateId(null), PolicySetHelper.COMB_ALG_FIRST_APPLICABLE, targetWizard.getXACML(), null);
        
        policyList = new LinkedList<XACMLObject>();
        policySetWizardList = new LinkedList<PolicySetWizard2>();
        
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

    public void addPolicySet(PolicySetWizard2 policySetWizard) {
        if (INSERTION_TYPE == InsertionType.AS_REFERENCE) {
            addPolicySetAsReference(policySetWizard);
        } else {
            addPolicySetAsWholeObject(policySetWizard);
        }
    }

    public List<XACMLObject> getPolicyTreeAsList() {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>(policyList);

        for (PolicySetWizard2 elem : policySetWizardList) {
            resultList.addAll(elem.getPolicyTreeAsList());
        }

        return resultList;
    }
    
    public TargetWizard getTargetWizard() {
        return targetWizard;
    }
    
    public boolean equals(Object policySetWizardObject) {
        
        if (!(policySetWizardObject instanceof PolicySetWizard2)) {
            return false;
        }
        
        PolicySetWizard2 policySetWizard = (PolicySetWizard2) policySetWizardObject;
        
        return targetWizard.equals(policySetWizard.getTargetWizard()); 
    }

    public PolicySetType getXACML() {
        return policySet;
    }
    
    public String toString() {
        return PolicySetHelper.toString(policySet);
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

    protected void addPolicySetAsReference(PolicySetWizard2 childPolicySetWizard) {
        PolicySetHelper.addPolicySetReference(this.policySet, childPolicySetWizard.getXACML().getPolicySetId());
        policySetWizardList.add(childPolicySetWizard);
    }

    protected void addPolicySetAsWholeObject(PolicySetType policySet) {
        PolicySetHelper.addPolicySet(this.policySet, policySet);
    }

    protected void addPolicySetAsWholeObject(PolicySetWizard2 policySetWiz) {
        addPolicySetAsWholeObject(policySetWiz.getXACML());
    }
}
