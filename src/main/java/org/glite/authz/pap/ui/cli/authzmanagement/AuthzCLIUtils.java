package org.glite.authz.pap.ui.cli.authzmanagement;

import org.glite.authz.pap.common.exceptions.VOMSSyntaxException;
import org.glite.authz.pap.common.utils.PathNamingScheme;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPPrincipal;


public class AuthzCLIUtils {
    
    public static PAPPrincipal principalFromString(String principalString){
        
        PAPPrincipal principal = new PAPPrincipal();
        
        try{
            
            PathNamingScheme.checkSyntax( principalString );
            principal.setType( "voms-fqan" );
            principal.setName( principalString );
            
        
        }catch(VOMSSyntaxException e){
            
            principal.setType( "x509-dn" );
            principal.setName( principalString );
            
        }
        
        return principal;
    }

   public static String[] permissionsFromString(String permString){
       
       String[] permissions;
       
       if (permString.contains( "|" ))
           permissions = permString.split( "|" );
       else 
           permissions = new String[]{permString};
       
       return permissions;
   }
    
}
