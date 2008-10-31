package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;


public class RemovePolicyOperation extends BasePAPOperation <Object>{


    String policyId;
    
    
    private RemovePolicyOperation(String policyId) {

        this.policyId = policyId;
    }
    
    
    public static RemovePolicyOperation instance(String policyId) {

        return new RemovePolicyOperation(policyId);
    }
    
    
    @Override
    protected Object doExecute() {
       
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        localPAP.deletePolicy(policyId);
        
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_WRITE ) );
        
    }
}
