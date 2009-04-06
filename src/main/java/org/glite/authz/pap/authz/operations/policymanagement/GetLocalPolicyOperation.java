package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicyType;

public class GetLocalPolicyOperation extends BasePAPOperation<PolicyType> {

    private Pap ps;
    private String policyId;

    protected GetLocalPolicyOperation(Pap ps, String policyId) {
        this.ps = ps;
        this.policyId = policyId;
    }

    public static GetLocalPolicyOperation instance(Pap ps, String policyId) {
        return new GetLocalPolicyOperation(ps, policyId);
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
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }
}
