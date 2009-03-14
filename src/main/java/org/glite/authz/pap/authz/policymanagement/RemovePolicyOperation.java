package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;

public class RemovePolicyOperation extends BasePAPOperation<Boolean> {

    String policyId;

    private RemovePolicyOperation(String policyId) {

        this.policyId = policyId;
    }

    public static RemovePolicyOperation instance(String policyId) {

        return new RemovePolicyOperation(policyId);
    }

    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        try {
            localPAP.deletePolicy(policyId);
        } catch (NotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }
}
