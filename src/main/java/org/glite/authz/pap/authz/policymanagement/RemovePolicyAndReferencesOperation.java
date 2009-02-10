package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;

public class RemovePolicyAndReferencesOperation extends BasePAPOperation<Boolean> {

    String policyId;

    private RemovePolicyAndReferencesOperation(String policyId) {

        this.policyId = policyId;
    }

    public static RemovePolicyAndReferencesOperation instance(String policyId) {

        return new RemovePolicyAndReferencesOperation(policyId);
    }

    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        if (!localPAP.hasPolicy(policyId))
            return false;
        
        localPAP.removePolicyAndReferences(policyId);
        
        return true;

    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }
}
