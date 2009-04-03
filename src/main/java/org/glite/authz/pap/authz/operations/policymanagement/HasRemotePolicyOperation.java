package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;

public class HasRemotePolicyOperation extends BasePAPOperation<Boolean> {

    private PAP ps;
    private String policyId;

    protected HasRemotePolicyOperation(PAP ps, String policyId) {
        this.ps = ps;
        this.policyId = policyId;
    }

    public static HasRemotePolicyOperation instance(PAP ps, String policyId) {
        return new HasRemotePolicyOperation(ps, policyId);
    }

    @Override
    protected Boolean doExecute() {
        PAPContainer localPAP = new PAPContainer(ps);
        return localPAP.hasPolicy(policyId);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
