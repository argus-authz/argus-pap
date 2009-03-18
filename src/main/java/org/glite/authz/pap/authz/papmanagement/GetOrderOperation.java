package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;

public class GetOrderOperation extends BasePAPOperation<String[]> {

    protected GetOrderOperation() {}

    public static GetOrderOperation instance() {
        return new GetOrderOperation();
    }

    @Override
    protected String[] doExecute() {

        String[] papOrderArray = null;
        papOrderArray = PAPManager.getInstance().getPAPConfigurationOrder();
        return papOrderArray;

    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_READ));

    }

}
