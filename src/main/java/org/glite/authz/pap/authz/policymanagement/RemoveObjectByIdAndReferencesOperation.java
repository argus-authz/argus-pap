package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class RemoveObjectByIdAndReferencesOperation extends BasePAPOperation<Boolean> {

    String id;

    private RemoveObjectByIdAndReferencesOperation(String id) {

        this.id = id;
    }

    public static RemoveObjectByIdAndReferencesOperation instance(String id) {

        return new RemoveObjectByIdAndReferencesOperation(id);
    }

    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getDefaultPAPContainer();

        if (localPAP.getPAPRootPolicySetId().equals(id)) {
            throw new RepositoryException("Invalid operation: cannot remove the local PAP root policy set");
        }

        if (localPAP.hasPolicy(id)) {
            localPAP.removePolicyAndReferences(id);
            return true;
        }

        if (localPAP.hasPolicySet(id)) {
            localPAP.removePolicySetAndReferences(id);
            return true;
        }

        return false;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }
}
