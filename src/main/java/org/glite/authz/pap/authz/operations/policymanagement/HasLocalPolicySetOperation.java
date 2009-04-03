package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;

public class HasLocalPolicySetOperation extends BasePAPOperation<Boolean> {

    private PAP ps;
    private String policySetId;

    protected HasLocalPolicySetOperation(PAP ps, String policySetId) {
        this.ps = ps;
        this.policySetId = policySetId;
    }

    public static HasLocalPolicySetOperation instance(PAP ps, String policySetId) {
        return new HasLocalPolicySetOperation(ps, policySetId);
    }

    @Override
    protected Boolean doExecute() {
        PAPContainer localPAP = new PAPContainer(ps);
        return localPAP.hasPolicySet(policySetId);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }
}
