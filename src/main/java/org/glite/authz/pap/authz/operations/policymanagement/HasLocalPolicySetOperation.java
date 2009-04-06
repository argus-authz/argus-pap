package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;

public class HasLocalPolicySetOperation extends BasePAPOperation<Boolean> {

    private Pap ps;
    private String policySetId;

    protected HasLocalPolicySetOperation(Pap ps, String policySetId) {
        this.ps = ps;
        this.policySetId = policySetId;
    }

    public static HasLocalPolicySetOperation instance(Pap ps, String policySetId) {
        return new HasLocalPolicySetOperation(ps, policySetId);
    }

    @Override
    protected Boolean doExecute() {
        PapContainer localPAP = new PapContainer(ps);
        return localPAP.hasPolicySet(policySetId);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }
}
