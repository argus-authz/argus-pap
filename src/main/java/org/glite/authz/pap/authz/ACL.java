package org.glite.authz.pap.authz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class ACL {

    Map <PAPAdmin, PAPPermission> permissions;

    
    
    public ACL() {

        permissions = new HashMap <PAPAdmin, PAPPermission>();
        
    }
    
    public Map <PAPAdmin, PAPPermission> getPermissions() {
    
        return permissions;
    }

    
    public void setPermissions( Map <PAPAdmin, PAPPermission> permissions ) {
    
        this.permissions = permissions;
    }
    
    
    public void setPermissions(PAPAdmin a, PAPPermission p ){
        
        permissions.put( a, p );
    }
    
    
    public void removePermissions(PAPAdmin a){
        permissions.remove( a );
    }
    
    @Override
    public String toString() {
        return "\n"+StringUtils.join(permissions.entrySet().iterator(), "\n");
        
    }
}
