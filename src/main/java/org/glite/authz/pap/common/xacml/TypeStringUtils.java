package org.glite.authz.pap.common.xacml;

import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class TypeStringUtils {
	
	public static PolicySetTypeString cloneAsPolicySetTypeString(PolicySetType policySet) {
		PolicySetTypeString policySetTypeString;

		if (policySet instanceof PolicySetTypeString) {
			policySetTypeString = new PolicySetTypeString(((PolicySetTypeString) policySet).getPolicySetString());
		} else {
			policySetTypeString = new PolicySetTypeString(PolicySetHelper.getInstance().clone(policySet));
			policySetTypeString.releaseDOM();
		}
		return policySetTypeString;
	}
	
	public static PolicyTypeString cloneAsPolicyTypeString(PolicyType policy) {
		PolicyTypeString policyTypeString;

		if (policy instanceof PolicyTypeString) {
			policyTypeString = new PolicyTypeString(((PolicyTypeString) policy).getPolicyString());
		} else {
			policyTypeString = new PolicyTypeString(PolicyHelper.getInstance().clone(policy));
			policyTypeString.releaseDOM();
		}
		return policyTypeString;
	}
	
	public static PolicySetTypeString getAsPolicySetTypeString(PolicySetType policySet) {
		if (policySet instanceof PolicyTypeString) {
			return (PolicySetTypeString) policySet;
		}
		return new PolicySetTypeString(policySet);
	}
	
	public static PolicyTypeString getAsPolicyTypeString(PolicyType policy) {
		if (policy instanceof PolicyTypeString) {
			return (PolicyTypeString) policy;
		}
		return new PolicyTypeString(policy);
	}
	
	public static void releaseUnusedMemory(Object object) {
    	if (object instanceof PolicyTypeString) {
    		((PolicyTypeString) object).releaseDOM();
    	} else if (object instanceof PolicySetTypeString) {
    		((PolicySetTypeString) object).releaseDOM();
    	} else if (object instanceof PolicyType) {
    		((PolicyType) object).releaseChildrenDOM(true);
    		((PolicyType) object).releaseDOM();
    	} else if (object instanceof PolicySetType) {
    		((PolicySetType) object).releaseChildrenDOM(true);
    		((PolicySetType) object).releaseDOM();
    	}
    }
}
