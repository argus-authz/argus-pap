package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;

public class HasLocalPolicyOperation extends BasePAPOperation<Boolean> {

    private Pap ps;
    private String policyId;

    protected HasLocalPolicyOperation(Pap ps, String policyId) {
        this.ps = ps;
        this.policyId = policyId;
    }

    public static HasLocalPolicyOperation instance(Pap ps, String policyId) {
        return new HasLocalPolicyOperation(ps, policyId);
    }

    @Override
    protected Boolean doExecute() {
        PapContainer localPAP = new PapContainer(ps);
        return localPAP.hasPolicy(policyId);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }
}
