package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;


public class RemovePapOperation extends BasePAPOperation <Boolean> {

    String papAlias;
    
    
    protected RemovePapOperation(String papAlias){
        this.papAlias = papAlias;
    }
    
    public static RemovePapOperation instance(String papAlias) {

        return new RemovePapOperation(papAlias);
    }
    
    @Override
    protected Boolean doExecute() {

        try {
            PapManager.getInstance().deletePap( papAlias );
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
