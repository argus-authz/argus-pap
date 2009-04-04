package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class GetRemoteRootPolicySetOperation extends BasePAPOperation<PolicySetType> {

    private PAP ps;

    protected GetRemoteRootPolicySetOperation(PAP ps) {
        this.ps = ps;
    }

    public static GetRemoteRootPolicySetOperation instance(PAP ps) {
        return new GetRemoteRootPolicySetOperation(ps);
    }

    @Override
    protected PolicySetType doExecute() {

        PAPContainer psContainer = new PAPContainer(ps);

        PolicySetType policySet = psContainer.getPAPRootPolicySet();

        return policySet;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
