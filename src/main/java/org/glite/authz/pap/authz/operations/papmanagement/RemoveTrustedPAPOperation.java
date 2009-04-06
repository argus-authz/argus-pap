package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;


public class RemoveTrustedPAPOperation extends BasePAPOperation <Boolean> {

    String papAlias;
    
    
    protected RemoveTrustedPAPOperation(String papAlias){
        this.papAlias = papAlias;
    }
    
    public static RemoveTrustedPAPOperation instance(String papAlias) {

        return new RemoveTrustedPAPOperation(papAlias);
    }
    
    @Override
    protected Boolean doExecute() {

        try {
            PapManager.getInstance().deletePAP( papAlias );
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
