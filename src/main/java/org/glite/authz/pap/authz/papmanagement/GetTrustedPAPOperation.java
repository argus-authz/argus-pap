package org.glite.authz.pap.authz.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;


public class GetTrustedPAPOperation extends BasePAPOperation<PAP> {

    String papId;
    
    protected GetTrustedPAPOperation(String papId){
        
        this.papId = papId;
    }
    
    public static GetTrustedPAPOperation instance(String papId) {

        return new GetTrustedPAPOperation(papId);
    }
    
    @Override
    protected PAP doExecute() {

        return PAPManager.getInstance().getPAP( papId );
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
