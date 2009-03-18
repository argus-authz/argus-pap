package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;


public class HasPolicySetOperation extends BasePAPOperation <Boolean> {

    String alias;
    String policySetId;
    
    protected HasPolicySetOperation( String alias, String policySetId ) {
        this.alias = alias;
        this.policySetId = policySetId;
    }

    
    public static HasPolicySetOperation instance(String alias, String policySetId) {

        return new HasPolicySetOperation(alias, policySetId);
    }
    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getPAPContainer(alias);
        return localPAP.hasPolicySet( policySetId );
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL) );
        
    }

}
