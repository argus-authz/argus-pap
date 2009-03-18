package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicyType;

public class UpdatePolicyOperation extends BasePAPOperation<Boolean> {

    String alias;
    String version;
    PolicyType policy;

    private UpdatePolicyOperation(String alias, String version, PolicyType policy) {

        this.alias = alias;
        this.version = version;
        this.policy = policy;
    }

    public static UpdatePolicyOperation instance(String alias, String version, PolicyType policy) {

        return new UpdatePolicyOperation(alias, version, policy);
    }

    @Override
    protected Boolean doExecute() {

        PAP pap = PAPManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

        if (!(papContainer.hasPolicy(policy.getPolicyId()))) {
            return false;
        }

        papContainer.updatePolicy(version, policy);

        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
