package org.glite.authz.pap.common.xacml;

import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeStringUtils {
    
    private static final Logger log = LoggerFactory.getLogger(TypeStringUtils.class);
    
	public static PolicySetTypeString cloneAsPolicySetTypeString(PolicySetType policySet) {
		PolicySetTypeString policySetTypeString;

		if (policySet instanceof PolicySetTypeString) {
		    
			policySetTypeString = new PolicySetTypeString(policySet.getPolicySetId(), ((PolicySetTypeString) policySet).getPolicySetString());
			
		} else {
		    
			policySetTypeString = new PolicySetTypeString(PolicySetHelper.getInstance().clone(policySet));
			policySetTypeString.releaseDOM();
			
			log.debug("Cloning a PolicySetType into a PolicySetTypeString");
		}
		return policySetTypeString;
	}
	
	public static PolicyTypeString cloneAsPolicyTypeString(PolicyType policy) {
		PolicyTypeString policyTypeString;

		if (policy instanceof PolicyTypeString) {
		    
			policyTypeString = new PolicyTypeString(policy.getPolicyId(), ((PolicyTypeString) policy).getPolicyString());
			
		} else {
		    
			policyTypeString = new PolicyTypeString(PolicyHelper.getInstance().clone(policy));
			policyTypeString.releaseDOM();
			
		}
		return policyTypeString;
	}
	
	public static PolicySetTypeString getAsPolicySetTypeString(PolicySetType policySet) {
	    
		if (policySet instanceof PolicySetTypeString) {
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
	
	public static void releaseUnneededMemory(Object object) {
	    
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
    		
    	} else if (object instanceof PolicyWizard) {
    	    
    	    ((PolicyWizard) object).releaseChildrenDOM();
    	    ((PolicyWizard) object).releaseDOM();
    	    
    	} else if (object instanceof PolicySetWizard) {
    	    
    	    ((PolicySetWizard) object).releaseChildrenDOM();
            ((PolicySetWizard) object).releaseDOM();
        } else {
            log.warn("releaseUnnecessaryMemory(): unknown object " + object.getClass().getName());
        }
    }
}
