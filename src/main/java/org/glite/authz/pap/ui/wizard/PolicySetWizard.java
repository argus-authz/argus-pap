package org.glite.authz.pap.ui.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
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
    
    @Deprecated
	public static PolicySetType build() {
	    return PolicySetHelper.buildWithAnyTarget(PAP.localPAPId, PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
	}
    
	protected final List<XACMLObject> policyTreeAsList;
	protected final List<PolicySetWizard> policySetWizardList;
	protected final PolicySetType policySet;
	
	protected PolicySetWizard(String policySetId, String policyCombiningAlgorithmId, TargetType target, ObligationsType obligations) {
	    policyTreeAsList = new LinkedList<XACMLObject>();
	    policySetWizardList = new LinkedList<PolicySetWizard>();
	    if ((target == null) && (obligations == null)) {
	        policySet = PolicySetHelper.buildWithAnyTarget(policySetId, policyCombiningAlgorithmId);
	    } else {
	        policySet = PolicySetHelper.build(policySetId, policyCombiningAlgorithmId, target, obligations);
	    }
	    policyTreeAsList.add(policySet);
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
        } else  {
            addPolicySetAsWholeObject(policySetWiz);
        }
    }
    
    public PolicySetType getPolicySetType() {
        return policySet;
    }
    
    public List<XACMLObject> getPolicyTreeAsList() {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>(policyTreeAsList);
        for (PolicySetWizard elem:policySetWizardList) {
            resultList.addAll(elem.getPolicyTreeAsList());
        }
        return resultList;
    }
    
    protected void addPolicyAsReference(PolicyType policy) {
	    PolicySetHelper.addPolicyReference(policySet, policy.getPolicyId());
        policyTreeAsList.add(policy);
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
    
//    protected void addPolicySetAsReference(PolicySetType policySet) {
//        PolicySetHelper.addPolicySetReference(this.policySet, policySet.getPolicySetId());
//        policyTreeAsListList.add(policySet);
//    }
    
    protected void addPolicySetAsReference(PolicySetWizard policySetWiz) {
        PolicySetType policySetT = policySetWiz.getPolicySetType();
        PolicySetHelper.addPolicySetReference(this.policySet, policySetT.getPolicySetId());
        policyTreeAsList.add(policySetT);
        policySetWizardList.add(policySetWiz);
    }
    
    protected void addPolicySetAsWholeObject(PolicySetType policySet) {
        PolicySetHelper.addPolicySet(this.policySet, policySet);
    }
	
    protected void addPolicySetAsWholeObject(PolicySetWizard policySetWiz) {
        addPolicySetAsWholeObject(policySetWiz.getPolicySetType());
    }
}
