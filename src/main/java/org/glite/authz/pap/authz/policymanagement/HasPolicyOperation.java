package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;


public class HasPolicyOperation extends BasePAPOperation <Boolean> {

    String alias;
    String policyId;
    
    protected HasPolicyOperation( String alias, String policyId ) {

        this.alias = alias;
        this.policyId = policyId;

    }

    public static HasPolicyOperation instance(String alias, String policyId) {

        return new HasPolicyOperation(alias, policyId);
    }
    
    
    @Override
    protected Boolean doExecute() {
        
        if (alias == null) {
            alias = PAP.DEFAULT_PAP_ALIAS;
        }
        
        PAPContainer localPAP = PAPManager.getInstance().getPAPContainer(alias);
        return localPAP.hasPolicy( policyId );
        
    }

    @Override
    protected void setupPermissions() {
        
        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_LOCAL ) );
        
    }
    
    

}
