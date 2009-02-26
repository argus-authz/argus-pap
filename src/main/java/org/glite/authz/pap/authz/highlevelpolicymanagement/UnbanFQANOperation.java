package org.glite.authz.pap.authz.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.wizard.BanAttributePolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.opensaml.xacml.policy.PolicyType;

public class UnbanFQANOperation extends BasePAPOperation<UnbanResult> {

    String fqan;

    protected UnbanFQANOperation(String fqan) {
        this.fqan = fqan;
    }

    public static UnbanFQANOperation instance(String fqan) {
        return new UnbanFQANOperation(fqan);
    }

    protected UnbanResult doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        List<PolicyType> policyList = localPAP.getAllPolicies();
        
        List<PolicyWizard> banPolicyList = BanAttributePolicyWizard.getBanPolicies(AttributeWizardType.FQAN, fqan, policyList);
        
        for (PolicyWizard policyWizard : banPolicyList) {
            localPAP.removePolicyAndReferences(policyWizard.getPolicyId());
        }
        
        UnbanResult unbanResult = new UnbanResult();
        unbanResult.setConflictingPolicies(new String[0]);

        log.info("UnbanFQAN request, number of policies removed: " + banPolicyList.size());
        
        if (banPolicyList.size() == 0) {
            unbanResult.setStatusCode(1);
        } else {
            unbanResult.setStatusCode(0);
        }
        
        return unbanResult;
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE, PermissionFlags.POLICY_READ_LOCAL));

    }

}
