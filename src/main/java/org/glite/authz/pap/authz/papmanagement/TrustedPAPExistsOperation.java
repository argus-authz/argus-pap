package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;


public class TrustedPAPExistsOperation extends BasePAPOperation<Boolean> {

    
    String papId;
    
    protected TrustedPAPExistsOperation(String papId) {

        this.papId = papId;
    }
    
    public static TrustedPAPExistsOperation instance(String papId) {

        return new TrustedPAPExistsOperation(papId);
    }
    
    
    @Override
    protected Boolean doExecute() {

        return PAPManager.getInstance().exists( papId );
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
