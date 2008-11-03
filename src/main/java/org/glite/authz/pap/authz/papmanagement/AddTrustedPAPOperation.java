package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;


public class AddTrustedPAPOperation extends BasePAPOperation {

    PAP pap;
    
    
    protected AddTrustedPAPOperation(PAP pap) {

        this.pap = pap;
        
    }
    
    public static AddTrustedPAPOperation instance(PAP pap) {

        return new AddTrustedPAPOperation(pap);
    }
    
    
    @Override
    protected Object doExecute() {

        PAPManager.getInstance().addTrustedPAP( pap );
        return null;
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ,PermissionFlags.CONFIGURATION_WRITE ) );

    }

}
