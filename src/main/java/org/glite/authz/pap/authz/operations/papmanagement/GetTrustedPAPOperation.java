package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapManager;


public class GetTrustedPAPOperation extends BasePAPOperation<Pap> {

    String papId;
    
    protected GetTrustedPAPOperation(String papId){
        
        this.papId = papId;
    }
    
    public static GetTrustedPAPOperation instance(String papId) {

        return new GetTrustedPAPOperation(papId);
    }
    
    @Override
    protected Pap doExecute() {
        
        Pap pap = PapManager.getInstance().getPap( papId );
        return pap;
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
