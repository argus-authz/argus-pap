package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class GetPAPPolicySetOperation extends BasePAPOperation<PolicySetType> {

    String papAlias;
    String policySetId;

    protected GetPAPPolicySetOperation(String papAlias, String policySetId) {

        this.papAlias = papAlias;
        this.policySetId = policySetId;

    }

    public static GetPAPPolicySetOperation instance(String papAlias, String policySetId) {
        return new GetPAPPolicySetOperation(papAlias, policySetId);
    }

    @Override
    protected PolicySetType doExecute() {

        PAPContainer pap = PAPManager.getInstance().getTrustedPAPContainer(papAlias);
        
        PolicySetType policySet = pap.getPolicySet(policySetId);
        
        return policySet;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));

    }

}
