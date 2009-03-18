package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicySetType;

public class UpdatePolicySetOperation extends BasePAPOperation<Boolean> {

    String alias;
    String version;
    PolicySetType policySet;

    public UpdatePolicySetOperation(String alias, String version, PolicySetType policySet) {

        this.alias = alias;
        this.version = version;
        this.policySet = policySet;
    }

    public static UpdatePolicySetOperation instance(String alias, String version, PolicySetType policySet) {
        return new UpdatePolicySetOperation(alias, version, policySet);
    }

    @Override
    protected Boolean doExecute() {

        PAP pap = PAPManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

        if (!papContainer.hasPolicySet(policySet.getPolicySetId())) {
            return false;
        }

        papContainer.updatePolicySet(version, policySet);

        return true;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
