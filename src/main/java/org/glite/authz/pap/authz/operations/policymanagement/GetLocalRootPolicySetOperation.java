package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class GetLocalRootPolicySetOperation extends BasePAPOperation<PolicySetType> {

    private Pap ps;

    protected GetLocalRootPolicySetOperation(Pap ps) {
        this.ps = ps;
    }

    public static GetLocalRootPolicySetOperation instance(Pap ps) {
        return new GetLocalRootPolicySetOperation(ps);
    }

    @Override
    protected PolicySetType doExecute() {

        PapContainer psContainer = new PapContainer(ps);

        PolicySetType policySet = psContainer.getPAPRootPolicySet();

        return policySet;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }
}
