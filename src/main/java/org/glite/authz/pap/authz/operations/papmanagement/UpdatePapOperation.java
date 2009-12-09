package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.papmanagement.PapManagerException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;

public class UpdatePapOperation extends BasePAPOperation<Boolean> {

    Pap pap;

    protected UpdatePapOperation(Pap pap) {
        this.pap = pap;
    }

    public static UpdatePapOperation instance(Pap pap) {

        return new UpdatePapOperation(pap);
    }

    @Override
    protected Boolean doExecute() {

        if (pap.isDefaultPap()) {
            throw new PapManagerException("Forbidden operation: the default PAP is read-only.");
        }

        try {
            PapManager papManager = PapManager.getInstance();

            Pap persistedPap = papManager.getPap(pap.getAlias());

            persistedPap.setAll(pap);

            PapManager.getInstance().updatePap(persistedPap);

        } catch (NotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_READ,
                                               PermissionFlags.CONFIGURATION_WRITE));
    }
}
