package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;


public class RemovePolicySetOperation extends BasePAPOperation <Object> {

    String policySetId;
    
    
    protected RemovePolicySetOperation(String policySetId) {

        this.policySetId = policySetId;
    }
    
    public static RemovePolicySetOperation instance(String policySetId) {

        return new RemovePolicySetOperation(policySetId);
    }
    
    
    @Override
    protected Object doExecute() {
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        localPAP.deletePolicySet( policySetId );
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_WRITE ) );
        
    }
    
    

}
