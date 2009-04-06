package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.papmanagement.PapManagerException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;


public class UpdateTrustedPAPOperation extends BasePAPOperation<Boolean> {

    Pap pap;
    
    protected UpdateTrustedPAPOperation(Pap pap){
        this.pap = pap;
    }
        
    public static UpdateTrustedPAPOperation instance(Pap pap) {

        return new UpdateTrustedPAPOperation(pap);
    }
    
    @Override
    protected Boolean doExecute() {
    	
    	if (Pap.DEFAULT_PAP_ALIAS.equals(pap.getAlias())) {
    		throw new PapManagerException("Forbidden operation: the default PAP is read-only.");
    	}

        try {
            PapManager.getInstance().updatePap( pap );
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
