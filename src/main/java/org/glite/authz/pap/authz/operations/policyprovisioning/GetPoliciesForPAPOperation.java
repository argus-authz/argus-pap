package org.glite.authz.pap.authz.operations.policyprovisioning;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.repository.dao.ProvisioningServiceDAO;
import org.opensaml.xacml.XACMLObject;

/**
 * Authorized operation to fetch policies out of this PAP when the caller is
 * another PAP.
 * 
 */
public class GetPoliciesForPAPOperation extends
        BasePAPOperation <List <XACMLObject>> {

    private GetPoliciesForPAPOperation() {

    }

    /**
     * Returns an instance of this operation
     * @return
     */
    public static GetPoliciesForPAPOperation instance() {

        return new GetPoliciesForPAPOperation();
    }

    @Override
    protected List <XACMLObject> doExecute() {

        return ProvisioningServiceDAO.getInstance().papQuery();
    }

    @Override
    /**
     * The required permission for this operation is:
     * <code>POLICY_READ_LOCAL</code>
     */
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission
                .of( PermissionFlags.POLICY_READ_LOCAL ) );

    }

}
