package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;


public class HasPolicySetOperation extends BasePAPOperation <Boolean> {

    String policySetId;
    
    protected HasPolicySetOperation( String policySetId ) {
        this.policySetId = policySetId;
    }

    
    public static HasPolicySetOperation instance(String policySetId) {

        return new HasPolicySetOperation(policySetId);
    }
    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        return localPAP.hasPolicySet( policySetId );
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL) );
        
    }

}
