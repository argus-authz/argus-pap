package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.papmanagement.PAPManager;


public class TrustedPAPExistsOperation extends BasePAPOperation<Boolean> {

    
    String papAlias;
    
    protected TrustedPAPExistsOperation(String papAlias) {

        this.papAlias = papAlias;
    }
    
    public static TrustedPAPExistsOperation instance(String papAlias) {

        return new TrustedPAPExistsOperation(papAlias);
    }
    
    
    @Override
    protected Boolean doExecute() {

        return PAPManager.getInstance().exists( papAlias );
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
