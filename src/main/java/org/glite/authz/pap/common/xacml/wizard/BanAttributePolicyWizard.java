package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.opensaml.xacml.policy.PolicyType;

public class BanAttributePolicyWizard {

    private BanAttributePolicyWizard() {}

    public static List<PolicyWizard> getBanPolicies(AttributeWizardType attributeType, String attributeValue, List<PolicyType> policyList) {
        
        List<PolicyWizard> candidateBanPolicyList = new LinkedList<PolicyWizard>();

        for (PolicyType policy : policyList) {

            PolicyWizard policyWizard = new PolicyWizard(policy);

            if (AttributeWizardType.DN.equals(attributeType)) {

                if (policyWizard.isBanPolicyForDN(attributeValue)) {
                    candidateBanPolicyList.add(policyWizard);
                }
                
            } else {

                if (policyWizard.isBanPolicyForFQAN(attributeValue)) {
                    candidateBanPolicyList.add(policyWizard);
                }
            }
        }
        return candidateBanPolicyList;
    }
}
