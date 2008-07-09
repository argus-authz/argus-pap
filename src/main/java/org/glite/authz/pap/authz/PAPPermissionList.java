package org.glite.authz.pap.authz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


public class PAPPermissionList {
    
    protected List <PAPPermission> permissions = new ArrayList <PAPPermission>();
    
    
    private PAPPermissionList() {

        // TODO Auto-generated constructor stub
    }
    
    public void addPermission(PAPPermission p){
        
        // Silently ignore null additions
        if (p == null)
            return;
        
        permissions.add( p );
        
    }
    
    public boolean satisfies(PAPPermission other){
        
        for (PAPPermission perm: permissions){
            
            if (perm.satisfies( other ))
                return true;
        }
        
        return false;
    }

    public int size() {

        return permissions.size();
    }

    public static PAPPermissionList instance() {

        return new PAPPermissionList();
    }
    
    @Override
    public String toString() {
    
        return ToStringBuilder.reflectionToString( this );
    }
}
