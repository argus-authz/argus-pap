package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;

public class GetPAPPolicyOperation extends BasePAPOperation<PolicyType> {

    String papAlias;
    String policyId;

    protected GetPAPPolicyOperation(String papAlias, String policyId) {
        this.papAlias = papAlias;
        this.policyId = policyId;

    }

    public static GetPAPPolicyOperation instance(String papAlias, String policyId) {
        return new GetPAPPolicyOperation(papAlias, policyId);
    }

    @Override
    protected PolicyType doExecute() {

        PAPContainer pap = PAPManager.getInstance().getRemotePAPContainer(papAlias);

        PolicyType policy = pap.getPolicy(policyId);

        return policy;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));

    }

}
