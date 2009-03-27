package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class GetLocalRootPolicySetOperation extends BasePAPOperation<PolicySetType> {

    private PAP ps;

    protected GetLocalRootPolicySetOperation(PAP ps) {
        this.ps = ps;
    }

    public static GetLocalRootPolicySetOperation instance(PAP ps) {
        return new GetLocalRootPolicySetOperation(ps);
    }

    @Override
    protected PolicySetType doExecute() {

        PAPContainer psContainer = new PAPContainer(ps);

        PolicySetType policySet = psContainer.getPAPRootPolicySet();

        return policySet;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }
}
