package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.DistributionConfiguration;
import org.glite.authz.pap.distribution.DistributionModule;

public class SetPollingIntervalOperation extends BasePAPOperation<Object> {

    long seconds;

    protected SetPollingIntervalOperation(long seconds) {
        this.seconds = seconds;
    }

    public static SetPollingIntervalOperation instance(long seconds) {
        return new SetPollingIntervalOperation(seconds);
    }

    @Override
    protected  Object doExecute() {
        log.info("Setting polling interval to: " + seconds);
        
        DistributionConfiguration.getInstance().savePollInterval(seconds);
        
        DistributionModule.getInstance().setSleepTime(seconds);
        
        return null;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_WRITE));
    }
}
