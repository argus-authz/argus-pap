package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicySetType;

public class GetRemotePolicySetOperation extends BasePAPOperation<PolicySetType> {

    private PAP ps;
    private String policySetId;

    protected GetRemotePolicySetOperation(PAP ps, String policySetId) {
        this.ps = ps;
        this.policySetId = policySetId;
    }

    public static GetRemotePolicySetOperation instance(PAP ps, String policySetId) {
        return new GetRemotePolicySetOperation(ps, policySetId);
    }

    @Override
    protected PolicySetType doExecute() {

        PAPContainer papContainer = new PAPContainer(ps);

        if (!papContainer.hasPolicySet(policySetId))
            throw new NotFoundException("PolicySet '" + policySetId + "' not found.");

        PolicySetType policySet = papContainer.getPolicySet(policySetId);

        return policySet;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
