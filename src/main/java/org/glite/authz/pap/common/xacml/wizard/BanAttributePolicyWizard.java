package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class BanAttributePolicyWizard {

    private BanAttributePolicyWizard() {}

    public static PolicyWizard getPolicyWizard(AttributeWizardType attributeWizardType, String attributeValue, boolean isPublic,
            String description) {

        List<AttributeWizard> targetList = new LinkedList<AttributeWizard>();

        targetList.add(new AttributeWizard(attributeWizardType, attributeValue));
        targetList.add(new AttributeWizard(AttributeWizardType.RESOURCE_PS, "*"));

        PolicyWizard policyWizard = new PolicyWizard(targetList, null, EffectType.Deny);

        if (isPublic) {
            policyWizard.setPrivate(false);
        } else {
            policyWizard.setPrivate(true);
        }

        if (description != null) {
            if (description.length() > 0) {
                policyWizard.setDescription(description);
            }
        }

        return policyWizard;
    }
    
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
