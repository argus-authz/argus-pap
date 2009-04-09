package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.papmanagement.PapManager;


public class PapExistsOperation extends BasePAPOperation<Boolean> {

    
    String papAlias;
    
    protected PapExistsOperation(String papAlias) {

        this.papAlias = papAlias;
    }
    
    public static PapExistsOperation instance(String papAlias) {

        return new PapExistsOperation(papAlias);
    }
    
    
    @Override
    protected Boolean doExecute() {

        return PapManager.getInstance().exists( papAlias );
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );

    }

}
