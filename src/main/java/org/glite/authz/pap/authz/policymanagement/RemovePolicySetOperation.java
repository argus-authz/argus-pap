package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;

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
            alias = PAP.DEFAULT_PAP_ALIAS;
        }
        
        PAP pap = PAPManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

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
