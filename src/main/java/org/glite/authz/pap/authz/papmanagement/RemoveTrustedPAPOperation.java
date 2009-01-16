package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;


public class RemoveTrustedPAPOperation extends BasePAPOperation <Boolean> {

    String papId;
    
    
    protected RemoveTrustedPAPOperation(String papId){
        this.papId = papId;
    }
    
    public static RemoveTrustedPAPOperation instance(String papId) {

        return new RemoveTrustedPAPOperation(papId);
    }
    
    @Override
    protected Boolean doExecute() {

        try {
            PAPManager.getInstance().deleteTrustedPAP( papId );
        } catch (NotFoundException e) {
            return false;
        }
        
        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ, PermissionFlags.CONFIGURATION_WRITE ) );

    }

}
