package org.glite.authz.pap.authz.management;

import java.util.Map;

import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPAdmin;
import org.glite.authz.pap.authz.PAPContext;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;


public class GetACLOperation extends BasePAPOperation <Map <PAPAdmin, PAPPermission>> {

    PAPContext context;
    
    
    private GetACLOperation(String context) {

        this.context = AuthorizationEngine.instance().getGlobalContext();
    }
    
    public static GetACLOperation instance(String context) {

        return new GetACLOperation(context);
    }
    @Override
    protected Map <PAPAdmin, PAPPermission> doExecute() {

        return context.getAcl().getPermissions();
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );
        
    }

}
