package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;

public class SetOrderOperation extends BasePAPOperation<Boolean> {

    String[] aliasArray;

    protected SetOrderOperation(String[] aliasArray) {
        this.aliasArray = aliasArray;
    }

    public static SetOrderOperation instance(String[] aliasArray) {
        return new SetOrderOperation(aliasArray);
    }

    @Override
    protected Boolean doExecute() {

        PAPManager.getInstance().setPAPOrder(aliasArray);
        return true;

    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_WRITE));
    }

}
