package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.services.exceptions.XACMLPolicyManagementServiceException;

public class RemovePolicySetOperation extends BasePAPOperation<Boolean> {

    String alias;
    String policySetId;

    protected RemovePolicySetOperation(String alias, String policySetId) {

        this.alias = alias;
        this.policySetId = policySetId;
    }

    public static RemovePolicySetOperation instance(String alias, String policySetId) {
        return new RemovePolicySetOperation(alias, policySetId);
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
            papContainer.deletePolicySet(policySetId);
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
