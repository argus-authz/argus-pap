package org.glite.authz.pap.services;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.ui.wizard.BlacklistPolicySet;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicySet;
import org.opensaml.xacml.policy.PolicySetType;

public class HighLevelPolicyManagementServiceUtils {
    
    public static void addPolicy(PolicyWizard policy) {
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        PolicySetType policySet;
        if (policy.isBlacklistPolicy())
            policySet = (new BlacklistPolicySet()).getPolicySetType();
        else
            policySet = (new ServiceClassPolicySet()).getPolicySetType();
        
        if (localPAP.hasPolicySet(policySet.getPolicySetId()))
            policySet = localPAP.getPolicySet(policySet.getPolicySetId());
        
        localPAP.storePolicy(policy.getPolicyType());
        
        PolicySetHelper.addPolicyReference(policySet, policy.getPolicyType().getPolicyId());
        
        localPAP.storePolicySet(policySet);
        
    }
    
    public static boolean removePolicy(PolicyWizard policy) {
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        String policySetId;

        if (policy.isBlacklistPolicy())
            policySetId = BlacklistPolicySet.POLICY_SET_ID;
        else
            policySetId = ServiceClassPolicySet.POLICY_SET_ID;
        
        if (!localPAP.hasPolicySet(policySetId))
            return false;
        
        PolicySetType policySet = localPAP.getPolicySet(policySetId);
        
        String policyId = policy.getPolicyType().getPolicyId();
        if (PolicySetHelper.deletePolicyReference(policySet, policyId) == false)
            return false;
        
        localPAP.storePolicySet(policySet);
        localPAP.deletePolicy(policyId);
        
        return true;
    }

}
