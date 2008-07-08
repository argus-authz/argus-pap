package org.glite.authz.pap.authz.provisioning;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.repository.dao.ProvisioningServiceDAO;
import org.opensaml.xacml.XACMLObject;


public class GetPoliciesForPAPOperation extends
        BasePAPOperation <List <XACMLObject>> {

    private GetPoliciesForPAPOperation() {

        // TODO Auto-generated constructor stub
    }
    
    public static GetPoliciesForPAPOperation instance() {

        return new GetPoliciesForPAPOperation();
    }
    
    @Override
    protected List <XACMLObject> doExecute() {

        return ProvisioningServiceDAO.getInstance().papQuery();
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_LOCAL) );
        
    }

}
