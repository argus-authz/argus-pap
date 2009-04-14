package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;

public class SetEnabledOperation extends BasePAPOperation<Object> {

    private String alias;
    private boolean enabled;

    protected SetEnabledOperation(String alias, boolean enabled) {
        this.alias = alias;
        this.enabled = enabled;
    }

    public static SetEnabledOperation instance(String alias, boolean enabled) {

        return new SetEnabledOperation(alias, enabled);
    }

    @Override
    protected Object doExecute() {

        Pap pap = PapManager.getInstance().getPap(alias);

        pap.setEnabled(enabled);

        PapManager.getInstance().updatePap(pap);

        return null;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_WRITE));
    }
}
