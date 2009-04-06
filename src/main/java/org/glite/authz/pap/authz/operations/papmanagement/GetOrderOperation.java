package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.papmanagement.PapManager;

public class GetOrderOperation extends BasePAPOperation<String[]> {

    protected GetOrderOperation() {}

    public static GetOrderOperation instance() {
        return new GetOrderOperation();
    }

    @Override
    protected String[] doExecute() {

        String[] papOrderArray = null;
        papOrderArray = PapManager.getInstance().getPAPConfigurationOrder();
        return papOrderArray;

    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_READ));

    }

}
