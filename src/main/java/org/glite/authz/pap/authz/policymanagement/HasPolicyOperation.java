package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;


public class HasPolicyOperation extends BasePAPOperation <Boolean> {

    String policyId;
    
    protected HasPolicyOperation( String policyId ) {

        this.policyId = policyId;

    }

    @Override
    protected Boolean doExecute() {
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        return localPAP.hasPolicy( policyId );
        
    }

    @Override
    protected void setupPermissions() {
        
        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_LOCAL ) );
        
    }
    
    

}
