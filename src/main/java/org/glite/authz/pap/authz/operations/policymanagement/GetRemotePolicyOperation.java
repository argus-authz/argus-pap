package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicyType;

public class GetRemotePolicyOperation extends BasePAPOperation<PolicyType> {

    private PAP ps;
    private String policyId;

    protected GetRemotePolicyOperation(PAP ps, String policyId) {
        this.ps = ps;
        this.policyId = policyId;
    }

    public static GetRemotePolicyOperation instance(PAP ps, String policyId) {
        return new GetRemotePolicyOperation(ps, policyId);
    }

    @Override
    protected PolicyType doExecute() {

        PAPContainer papContainer = new PAPContainer(ps);

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
