package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;


public class AddTrustedPAPOperation extends BasePAPOperation <Boolean> {

    Pap pap;
    
    
    protected AddTrustedPAPOperation(Pap pap) {

        this.pap = pap;
        
    }
    
    public static AddTrustedPAPOperation instance(Pap pap) {

        return new AddTrustedPAPOperation(pap);
    }
    
    
    @Override
    protected Boolean doExecute() {
        
        log.info("Adding PAP: " + pap);
        
        try {
            
            PapManager.getInstance().addPap( pap );
            
        } catch (AlreadyExistsException e) {
            return false;
        }
        
        return true;
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_WRITE ) );

    }

}