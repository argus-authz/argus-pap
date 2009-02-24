package org.glite.authz.pap.authz.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.BanAttributePolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.BlacklistPolicySet;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.TargetWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class BanOperation extends BasePAPOperation<String> {

    AttributeWizard banAttributeWizard;
    AttributeWizard resourceAttributeWizard;
    AttributeWizard actionAttributeWizard;
    String attributeValue;
    String description;
    boolean isPublic;

    protected BanOperation(AttributeWizard banAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard, boolean isPublic, String description) {

        this.banAttributeWizard = banAttributeWizard;
        this.resourceAttributeWizard = resourceAttributeWizard;
        this.actionAttributeWizard = actionAttributeWizard;
        this.description = description;
        this.isPublic = isPublic;
    }

    public static BanOperation instance(AttributeWizard banAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard, boolean isPublic, String description) {
        return new BanOperation(banAttributeWizard, resourceAttributeWizard, actionAttributeWizard, isPublic, description);
    }

    protected String doExecute() {

        TargetWizard policySetTargetWizard = new TargetWizard(resourceAttributeWizard);

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        // get the policy set from the repository (the first one for which a match is found)
        PolicySetType targetPolicySet = null;
        List<PolicySetType> policySetList = localPAP.getAllPolicySets();

        for (PolicySetType policySet : policySetList) {
            if (policySetTargetWizard.isEqual(policySet.getTarget())) {
                targetPolicySet = policySet;
                break;
            }
        }

        boolean updateOperationForPolicySet = true;

        if (targetPolicySet == null) {
            updateOperationForPolicySet = false;
            targetPolicySet = (new PolicySetWizard(resourceAttributeWizard)).getXACML();
            log.debug("creating a new PolicySet for BanOperation");
        } else {
            log.debug(String.format("Found PolicySet \"%s\" for BanOperation", targetPolicySet.getPolicySetId()));
        }

        TargetWizard policyTargetWizard = new TargetWizard(actionAttributeWizard);
        PolicyWizard targetPolicyWizard = null;
        
        // get the first matching policy
        for (String policyId : PolicySetHelper.getPolicyIdReferencesValues(targetPolicySet)) {
            PolicyType policy = localPAP.getPolicy(policyId);
            if (policyTargetWizard.isEqual(policy.getTarget())) {
                targetPolicyWizard = new PolicyWizard(policy);
                break;
            }
        }
        
        boolean updateOperationForPolicy = true;
        if (targetPolicyWizard == null) {
            updateOperationForPolicy = false;
            targetPolicyWizard = new PolicyWizard(actionAttributeWizard); 
        }
        
        targetPolicyWizard.addRule(banAttributeWizard, EffectType.Deny);
        
        

        PolicyWizard policyWizard = BanAttributePolicyWizard.getPolicyWizard(AttributeWizardType.DN, attributeValue, isPublic,
                description);

        String policyId = policyWizard.getXACML().getPolicyId();

//        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        localPAP.addPolicy(BlacklistPolicySet.POLICY_SET_ID, policyWizard.getXACML());

        log.info("Added BlackList policy: " + policyId);

        return policyId;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
