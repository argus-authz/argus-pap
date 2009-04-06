package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;

public class RefreshPolicyCacheOperation extends BasePAPOperation<Boolean> {

    String papAlias;

    protected RefreshPolicyCacheOperation(String papAlias) {

        this.papAlias = papAlias;
    }

    public static RefreshPolicyCacheOperation instance(String papAlias) {

        return new RefreshPolicyCacheOperation(papAlias);
    }

    @Override
    protected Boolean doExecute() {

        PapManager papManager = PapManager.getInstance();

        PAP pap;

        try {
            pap = papManager.getPAP(papAlias);

        } catch (NotFoundException e) {
            log.error("Unable to refresh cache, PAP not found: " + papAlias);
            return false;
        }
        
        if (!(pap.isRemote())) {
            throw new PAPException("\"" + papAlias + "\" is local, nothing to refresh");
        }

        try {
            DistributionModule.refreshCache(pap);

        } catch (Throwable t) {
            throw new PAPException(t.getMessage(), t);
        }

        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE, PermissionFlags.POLICY_WRITE));

    }

}
