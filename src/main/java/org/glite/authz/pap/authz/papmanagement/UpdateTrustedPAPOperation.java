package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;


public class UpdateTrustedPAPOperation extends BasePAPOperation {

    String papId;
    PAP pap;
    
    protected UpdateTrustedPAPOperation(String papId, PAP pap){
        
        this.papId = papId;
        this.pap = pap;
        
    }
        
    public static UpdateTrustedPAPOperation instance(String papId, PAP pap) {

        return new UpdateTrustedPAPOperation(papId,pap);
    }
    
    @Override
    protected Object doExecute() {

        PAPManager.getInstance().updateTrustedPAP( papId, pap );
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ, PermissionFlags.CONFIGURATION_WRITE ) );

    }

}
