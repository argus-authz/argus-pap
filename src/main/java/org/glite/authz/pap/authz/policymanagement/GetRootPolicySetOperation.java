package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class GetRootPolicySetOperation extends BasePAPOperation<PolicySetType> {

    String papAlias;

    protected GetRootPolicySetOperation(String papAlias) {

        this.papAlias = papAlias;

    }

    public static GetRootPolicySetOperation instance(String papAlias) {
        return new GetRootPolicySetOperation(papAlias);
    }

    @Override
    protected PolicySetType doExecute() {

        PAPContainer pap;
        if (papAlias == null) {
        	pap = PAPManager.getInstance().getDefaultPAPContainer();
        } else {
        	pap = PAPManager.getInstance().getPAPContainer(papAlias);
        }
        
        PolicySetType policySet = pap.getPAPRootPolicySet();
        
        return policySet;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL, PermissionFlags.POLICY_READ_REMOTE));
    }
}
