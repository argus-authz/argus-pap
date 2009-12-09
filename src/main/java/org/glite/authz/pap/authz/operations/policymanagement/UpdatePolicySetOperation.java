package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.exceptions.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicySetType;

public class UpdatePolicySetOperation extends BasePAPOperation<Boolean> {

    private String alias;
    private String version;
    private PolicySetType policySet;

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

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }
        
        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        if (!papContainer.hasPolicySet(policySet.getPolicySetId())) {
            return false;
        }

        papContainer.updatePolicySet(policySet);

        return true;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE, PermissionFlags.POLICY_READ_LOCAL));
    }
}
