package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class GetRemoteRootPolicySetOperation extends BasePAPOperation<PolicySetType> {

    private Pap ps;

    protected GetRemoteRootPolicySetOperation(Pap ps) {
        this.ps = ps;
    }

    public static GetRemoteRootPolicySetOperation instance(Pap ps) {
        return new GetRemoteRootPolicySetOperation(ps);
    }

    @Override
    protected PolicySetType doExecute() {

        PapContainer psContainer = new PapContainer(ps);

        PolicySetType policySet = psContainer.getPAPRootPolicySet();

        return policySet;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
