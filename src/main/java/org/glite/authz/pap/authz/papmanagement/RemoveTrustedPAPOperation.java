package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;


public class RemoveTrustedPAPOperation extends BasePAPOperation {

    String papId;
    
    
    protected RemoveTrustedPAPOperation(String papId){
        this.papId = papId;
    }
    
    public static RemoveTrustedPAPOperation instance(String papId) {

        return new RemoveTrustedPAPOperation(papId);
    }
    
    @Override
    protected Object doExecute() {

        PAPManager.getInstance().deleteTrustedPAP( papId );
        
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ, PermissionFlags.CONFIGURATION_WRITE ) );

    }

}
