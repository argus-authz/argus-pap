package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;

public class RemoveObjectByIdAndReferencesOperation extends BasePAPOperation<Boolean> {

    String alias;
    String id;

    private RemoveObjectByIdAndReferencesOperation(String alias, String id) {
        this.alias = alias;
        this.id = id;
    }

    public static RemoveObjectByIdAndReferencesOperation instance(String alias, String id) {

        return new RemoveObjectByIdAndReferencesOperation(alias, id);
    }

    @Override
    protected Boolean doExecute() {

        PAP pap = PAPManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

        if (papContainer.getPAPRootPolicySetId().equals(id)) {
            throw new RepositoryException("Invalid operation: cannot remove the local PAP root policy set");
        }

        if (papContainer.hasPolicy(id)) {
            papContainer.removePolicyAndReferences(id);
            return true;
        }

        if (papContainer.hasPolicySet(id)) {
            papContainer.removePolicySetAndReferences(id);
            return true;
        }

        return false;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }
}
