package org.glite.authz.pap.services.authz_management;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;
import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.authz.PAPACE;
import org.glite.authz.pap.authz.PAPAdmin;
import org.glite.authz.pap.authz.PAPContext;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.VOMSFQAN;
import org.glite.authz.pap.authz.X509Principal;
import org.glite.authz.pap.authz.exceptions.PAPAuthzException;
import org.glite.authz.pap.authz.management.AddACEOperation;
import org.glite.authz.pap.authz.management.RemoveACEOperation;
import org.glite.authz.pap.common.utils.PathNamingScheme;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPACEListType;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPAuthorizationManagement;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPException;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPPermissionList;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPPrincipalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PAPAuthzManagementService implements PAPAuthorizationManagement{

    public static final Logger log = LoggerFactory.getLogger( PAPAuthzManagementService.class );
    

    protected PAPAdmin getPrincipal(PAPPrincipalType principal){
        
        if (principal.getType().getValue().equals( "x509-dn" )){
            
            return new X509Principal(principal.getName());
            
        }else if (principal.getType().getValue().equals( "voms-fqan" )){
            
            return new VOMSFQAN(principal.getName());
        }else
            throw new PAPAuthzException("Unsupported principal type '"+principal.getType().getValue()+"'.");
        
    }
    
    protected PAPPermission parsePermission(PAPPermissionList perms){
        
        if (perms == null)
            return PAPPermission.getEmptyPermission();
        else
    
            return PAPPermission.fromStringArray(  perms.getItem() );
        }
    
    protected void checkPAPPrincipal(PAPPrincipalType principal){
        
        if (principal == null)
            throw new PAPAuthzException("Null principal passed as argument!");
        
        if (principal.getType().getValue().equals( "x509-dn" )){
            
            if (principal.getName() == null || principal.getName().equals( "" ))
                throw new PAPAuthzException("X509 Principal with NULL or empty DN passed as argument!");
            
        }else if (principal.getType().getValue().equals( "voms-fqan" )){
            
            PathNamingScheme.checkSyntax( principal.getName());
        }
    }
    
    

    public PAPACEListType getACL( String context ) throws RemoteException ,
            PAPException {
        
        log.info( "getACL(" + StringUtils.join( new Object[] { context }, ',' )
                + ");" );
        

        throw new PAPAuthzException("Unimplemented feature!"); 
    }

    
    public void setACL( String context, PAPACEListType entries )
            throws RemoteException , PAPException {
        
        log.info( "setACL("
                + StringUtils.join( new Object[] { context, entries }, ',' )
                + ");" );
        
        throw new PAPAuthzException("Unimplemented feature!");
        
    }

    public void addACE(
            String context,
            PAPPrincipalType principal,
            PAPPermissionList permissions )
            throws RemoteException , PAPException {

        log.info( "addACE("
                + StringUtils.join( new Object[] { context, principal,
                        permissions }, ',' ) + ");" );
        
     
        
        PAPContext papContext = null;
        
        checkPAPPrincipal( principal );
        
        PAPAdmin admin = getPrincipal( principal );
        
        if (permissions == null || permissions.getItem() == null || permissions.getItem().length == 0)
            throw new PAPAuthzException("Cannot set NULL permissions for principal '"+admin+"'.");
        
        if (context == null || context.equals( "" ) || context.equals( "global-context" ))
            papContext = AuthorizationEngine.instance().getGlobalContext();
        else 
            throw new PAPAuthzException("Only the context 'global-context' is currently supported!");
        
        
        PAPPermission perms = parsePermission( permissions );
        
        AddACEOperation.instance( PAPACE.instance( papContext, admin, perms ) ).execute();
        
        
    }

    public void removeACE( String context, PAPPrincipalType principal )
            throws RemoteException , PAPException {

        PAPContext papContext = null;
        
        checkPAPPrincipal( principal );
        
        PAPAdmin admin = getPrincipal( principal );
        
        if (context == null || context.equals( "" ) || context.equals( "global-context" ))
            papContext = AuthorizationEngine.instance().getGlobalContext();
        else 
            throw new PAPAuthzException("Only the context 'global-context' is currently supported!");
        
        
        RemoveACEOperation.instance( PAPACE.instance( papContext, admin ) ).execute();
        
    }


    
}
