package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.distribution.PAPManagerException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;


public class UpdateTrustedPAPOperation extends BasePAPOperation<Boolean> {

    PAP pap;
    
    protected UpdateTrustedPAPOperation(PAPData papData){
        
        this.pap = new PAP(papData);
        
    }
        
    public static UpdateTrustedPAPOperation instance(PAPData papData) {

        return new UpdateTrustedPAPOperation(papData);
    }
    
    @Override
    protected Boolean doExecute() {
    	
    	if (PAP.DEFAULT_PAP_ALIAS.equals(pap.getAlias())) {
    		throw new PAPManagerException("Forbidden operation: the default PAP is read-only.");
    	}

        try {
            PAPManager.getInstance().updatePAP( pap );
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
