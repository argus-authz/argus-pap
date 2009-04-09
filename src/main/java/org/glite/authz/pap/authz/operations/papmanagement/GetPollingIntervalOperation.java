package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.DistributionConfiguration;

public class GetPollingIntervalOperation extends BasePAPOperation<Long> {

    protected GetPollingIntervalOperation() {}

    public static GetPollingIntervalOperation instance() {
        return new GetPollingIntervalOperation();
    }

    @Override
    protected Long doExecute() {
        return DistributionConfiguration.getInstance().getPollIntervall();
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_READ));
    }
}
