package org.glite.authz.pap.authz.highlevelpolicymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.wizard.BanAttributePolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.BlacklistPolicySet;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;

public class BanDNOperation extends BasePAPOperation<String> {

    String dn;
    String description;
    boolean isPublic;

    protected BanDNOperation(String dn, boolean isPublic, String description) {

        this.dn = dn;
        this.description = description;
        this.isPublic = isPublic;
    }

    public static BanDNOperation instance(String dn, boolean isPublic, String description) {
        return new BanDNOperation(dn, isPublic, description);
    }

    protected String doExecute() {

        PolicyWizard policyWizard = BanAttributePolicyWizard.getPolicyWizard(AttributeWizardType.DN, dn, isPublic, description);
        
        String policyId = policyWizard.getPolicyType().getPolicyId();
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        localPAP.addPolicy(BlacklistPolicySet.POLICY_SET_ID, policyWizard.getPolicyType());
        
        log.info("Added BlackList policy: " + policyId);
        
        return policyId;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}