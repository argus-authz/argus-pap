package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;

public class RemovePolicyOperation extends BasePAPOperation<Boolean> {

    String alias;
    String policyId;

    private RemovePolicyOperation(String alias, String policyId) {

        this.alias = alias;
        this.policyId = policyId;
    }

    public static RemovePolicyOperation instance(String alias, String policyId) {

        return new RemovePolicyOperation(alias, policyId);
    }

    @Override
    protected Boolean doExecute() {

        PAP pap = PAPManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

        try {
            papContainer.deletePolicy(policyId);
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
