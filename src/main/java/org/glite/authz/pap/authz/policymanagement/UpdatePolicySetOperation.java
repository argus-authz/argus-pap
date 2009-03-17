package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class UpdatePolicySetOperation extends BasePAPOperation<Boolean> {

    String version;
    PolicySetType policySet;

    public UpdatePolicySetOperation(String version, PolicySetType policySet) {

        this.version = version;
        this.policySet = policySet;
    }

    public static UpdatePolicySetOperation instance(String version, PolicySetType policySet) {
        return new UpdatePolicySetOperation(version, policySet);
    }

    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getDefaultPAPContainer();

        if (!localPAP.hasPolicySet(policySet.getPolicySetId())) {
            return false;
        }

        localPAP.updatePolicySet(version, policySet);

        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
