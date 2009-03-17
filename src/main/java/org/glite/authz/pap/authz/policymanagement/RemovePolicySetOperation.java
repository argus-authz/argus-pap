package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;

public class RemovePolicySetOperation extends BasePAPOperation<Boolean> {

    String policySetId;

    protected RemovePolicySetOperation(String policySetId) {

        this.policySetId = policySetId;
    }

    public static RemovePolicySetOperation instance(String policySetId) {

        return new RemovePolicySetOperation(policySetId);
    }

    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getDefaultPAPContainer();

        try {
            localPAP.deletePolicySet(policySetId);
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
