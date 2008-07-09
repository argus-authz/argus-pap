package org.glite.authz.pap.authz.provisioning;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.repository.dao.ProvisioningServiceDAO;
import org.opensaml.xacml.XACMLObject;


public class GetPoliciesForPDPOperation extends BasePAPOperation<List<XACMLObject>> {
  
    private GetPoliciesForPDPOperation() {

        
    }
    
    public static GetPoliciesForPDPOperation instance() {

        return new GetPoliciesForPDPOperation();
    }
    
    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_LOCAL, PermissionFlags.POLICY_READ_REMOTE ) );

    }

    @Override
    protected List <XACMLObject> doExecute() {

        return ProvisioningServiceDAO.getInstance().pdpQuery(); 
    }
    
}
