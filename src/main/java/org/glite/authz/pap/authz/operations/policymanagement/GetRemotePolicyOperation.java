package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicyType;

public class GetRemotePolicyOperation extends BasePAPOperation<PolicyType> {

    private Pap ps;
    private String policyId;

    protected GetRemotePolicyOperation(Pap ps, String policyId) {
        this.ps = ps;
        this.policyId = policyId;
    }

    public static GetRemotePolicyOperation instance(Pap ps, String policyId) {
        return new GetRemotePolicyOperation(ps, policyId);
    }

    @Override
    protected PolicyType doExecute() {

        PapContainer papContainer = new PapContainer(ps);

        if (!papContainer.hasPolicy(policyId)) {
            throw new NotFoundException("Policy '" + policyId + "' not found.");
        }

        PolicyType policy = papContainer.getPolicy(policyId);

        return policy;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
