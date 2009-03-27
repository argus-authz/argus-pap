package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;

public class HasRemotePolicySetOperation extends BasePAPOperation<Boolean> {

    private PAP ps;
    private String policySetId;

    protected HasRemotePolicySetOperation(PAP ps, String policySetId) {
        this.ps = ps;
        this.policySetId = policySetId;
    }

    public static HasRemotePolicySetOperation instance(PAP ps, String policySetId) {
        return new HasRemotePolicySetOperation(ps, policySetId);
    }

    @Override
    protected Boolean doExecute() {
        PAPContainer localPAP = new PAPContainer(ps);
        return localPAP.hasPolicySet(policySetId);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
