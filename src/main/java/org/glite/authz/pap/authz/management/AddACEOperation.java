package org.glite.authz.pap.authz.management;

import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPACE;
import org.glite.authz.pap.authz.PAPAdmin;
import org.glite.authz.pap.authz.PAPContext;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.authz.exceptions.PAPAuthzException;


public class AddACEOperation extends BasePAPOperation <Object> {

    PAPACE ace;
    
    private AddACEOperation(PAPACE ace) {

        this.ace = ace;
        
    }
    
    public static AddACEOperation instance(PAPACE ace) {

        return new AddACEOperation(ace);
    }
    
    @Override
    protected Object doExecute() {

        PAPContext ctxt = ace.getContext();
        
        if (ctxt == null)
            ctxt = AuthorizationEngine.instance().getGlobalContext();
        
        PAPAdmin admin = ace.getAdmin();
        
        if (admin == null)
            throw new PAPAuthzException("Cannot setup permissions for NULL admins!");
        
        PAPPermission perm = ace.getPerms();
        ctxt.getAcl().setPermissions( admin, perm );
        
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_WRITE, PermissionFlags.CONFIGURATION_READ ) );
        
    }

}
