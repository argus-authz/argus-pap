package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.services.exceptions.XACMLPolicyManagementServiceException;

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

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }
        
        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

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
