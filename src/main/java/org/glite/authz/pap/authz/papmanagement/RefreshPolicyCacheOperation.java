package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;

public class RefreshPolicyCacheOperation extends BasePAPOperation<Boolean> {

    String papId;

    protected RefreshPolicyCacheOperation(String papId) {

        this.papId = papId;
    }

    public static RefreshPolicyCacheOperation instance(String papId) {

        return new RefreshPolicyCacheOperation(papId);
    }

    @Override
    protected Boolean doExecute() {

        PAPManager papManager = PAPManager.getInstance();

        PAP pap;

        try {
            pap = papManager.getPAP(papId);

        } catch (NotFoundException e) {
            log.error("Unable to refresh cache, PAP not found: " + papId);
            return false;
        }

        try {
            DistributionModule.refreshCache(pap);

        } catch (Throwable t) {

            log.error("Error contact remote pap '" + pap.getAlias() + "' for cache refresh!", t);
            throw new PAPException("Error contact remote pap '" + pap.getAlias() + "' for cache refresh!", t);
        }

        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE, PermissionFlags.POLICY_WRITE));

    }

}