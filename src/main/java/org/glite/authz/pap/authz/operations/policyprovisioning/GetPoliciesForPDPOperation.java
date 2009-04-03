package org.glite.authz.pap.authz.operations.policyprovisioning;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.repository.dao.ProvisioningServiceDAO;
import org.opensaml.xacml.XACMLObject;

/**
 * Authorized operation to fetch policies out of this pap when the caller
 * is a PDP.
 *
 */
public class GetPoliciesForPDPOperation extends
	BasePAPOperation<List<XACMLObject>> {

    private GetPoliciesForPDPOperation() {

    }

    public static GetPoliciesForPDPOperation instance() {

	return new GetPoliciesForPDPOperation();
    }

    @Override
    protected List<XACMLObject> doExecute() {

	return ProvisioningServiceDAO.getInstance().pdpQuery();
    }

    @Override
    /**
     * The required permission for this operation is:
     * <code>POLICY_READ_LOCAL|POLICY_READ_REMOTE</code>
     */
    protected void setupPermissions() {

	addRequiredPermission(PAPPermission.of(
		PermissionFlags.POLICY_READ_LOCAL,
		PermissionFlags.POLICY_READ_REMOTE));

    }

}
