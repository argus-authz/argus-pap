package org.glite.authz.pap.authz;

import java.util.HashMap;
import java.util.Map;

import org.glite.authz.pap.authz.exceptions.PAPAuthzException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BasePAPOperation <T> implements PAPOperation<T>{

    Logger log = LoggerFactory.getLogger( BasePAPOperation.class );
    
    protected Map <PAPContext, PAPPermission> requiredPermission;
    
    
    protected BasePAPOperation() {

        requiredPermission = new HashMap <PAPContext, PAPPermission>();
    }
    
    private boolean permissionsInitialized(){
        
        if (requiredPermission == null || requiredPermission.isEmpty())
            return false;
        
        return true;
        
    }
    
    public T execute() {

        logOperation();
        
        if (!isAllowed()){
            
            log.info("Insufficient privileges to perform operation '"+getName()+"'");
            throw new PAPAuthzException("Insufficient privileges to perform operation '"+getName()+"'.");
        }
        
        return doExecute();
    }

    
    
    public Map <PAPContext, PAPPermission> getRequiredPermission() {

        if (!permissionsInitialized())
            setupPermissions();
        
        return requiredPermission;
    }

    protected abstract T doExecute();
    
    
    protected final void addRequiredPermission(PAPContext context, PAPPermission perms){
        assert context != null: "Cannot add permission for a null context!";
        assert perms != null: "Cannot add null permissions for a context!";
        
        requiredPermission.put( context, perms );
        
    }
    
    protected final void addRequiredPermission(PAPPermission perms){
        assert perms != null: "Cannot add null permissions for the global context!";
        assert AuthorizationEngine.instance().isInitialized(): "Authz engine not initialized! Cannot get global context!";
        
        requiredPermission.put( AuthorizationEngine.instance().getGlobalContext(), perms );    
    }
    
    protected String getName(){
        
        return this.getClass().getSimpleName();
        
    }
    
    public final boolean isAllowed(){
        
        CurrentAdmin admin = CurrentAdmin.instance();
        
        if (! permissionsInitialized())
            setupPermissions();
        
        if (requiredPermission.isEmpty())
            throw new PAPAuthzException("No required permissions defined for operation '"+getName()+"'.");
        
        for ( Map.Entry <PAPContext, PAPPermission> entry: requiredPermission.entrySet() ) {
            
            PAPContext context = entry.getKey();
            ACL acl = entry.getKey().getAcl();
            PAPPermission perms = entry.getValue();
            
            if (acl == null)
                throw new  PAPAuthzException("No ACL defined for context '"+entry.getKey()+"'.");
            
            if (!admin.hasPermissions( context, perms))
                return false;   
        }
        
        return true;
        
    }
    
    
    protected void logOperation(){
        
        // TODO: implement me!
    }
    
    protected abstract void setupPermissions();
        
}
