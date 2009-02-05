package org.glite.authz.pap.authz.management;

import java.util.HashMap;
import java.util.Map;

import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPAdmin;
import org.glite.authz.pap.authz.PAPContext;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;


public class SetACLOperation extends BasePAPOperation <Object> {

    
    PAPContext context;
    Map<PAPAdmin, PAPPermission> permissions;
    
    
    public SetACLOperation(String context, Map<PAPAdmin, PAPPermission> permissions) {
        
        this.context = AuthorizationEngine.instance().getGlobalContext();
        this.permissions = permissions;
        
    }
    
    public static SetACLOperation instance(String context, Map<PAPAdmin, PAPPermission> permissions) {
        return new SetACLOperation(context, permissions);
    }
    
    protected Map<PAPAdmin, PAPPermission> clonePermissions(Map<PAPAdmin, PAPPermission> toBeCloned){
        
        HashMap <PAPAdmin, PAPPermission> clonedMap = (HashMap<PAPAdmin, PAPPermission>)((HashMap <PAPAdmin, PAPPermission>)toBeCloned).clone();
        return clonedMap;
    }
    @Override
    protected Object doExecute() {

        Map<PAPAdmin, PAPPermission> oldPerms = clonePermissions( context.getAcl().getPermissions());
        
        try{
            
            context.getAcl().getPermissions().clear();
            context.getAcl().setPermissions( permissions );
            
        }catch(Throwable t){
            
            log.error( "Error setting ACL! "+t.getMessage(), t );
            log.error( "Restoring previous permissions...");
            context.getAcl().setPermissions( oldPerms );
            
        }
        
        oldPerms.clear();
        return null;   
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ, PermissionFlags.CONFIGURATION_WRITE ) );
        
    }
    
    

}
