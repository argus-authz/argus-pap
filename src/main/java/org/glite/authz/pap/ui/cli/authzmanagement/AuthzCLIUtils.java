package org.glite.authz.pap.ui.cli.authzmanagement;

import org.apache.commons.lang.StringUtils;
import org.glite.authz.pap.common.exceptions.VOMSSyntaxException;
import org.glite.authz.pap.common.utils.PathNamingScheme;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPPrincipal;
import org.glite.security.util.DN;
import org.glite.security.util.DNHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthzCLIUtils {
    
    public static final String ANY_AUTHENTICATED_USER_DN = "/O=PAP/OU=Internal/CN=Any authenticated user";
    public static final Logger log = LoggerFactory.getLogger( AuthzCLIUtils.class );
    
    public static String convertX500SubjectToRFC2253(String x500Subject){
    	
    	DN dn = DNHandler.getDN(x500Subject);
    	
    	return dn.getRFC2253v2();
    }
    
    
    public static String convertRFC2253toX500Subject(String rfc2253Subject){
    	
    	DN dn = DNHandler.getDN(rfc2253Subject);
    	
    	return dn.getX500();
    	
    }
    public static PAPPrincipal principalFromString(String principalString){
        
        PAPPrincipal principal = new PAPPrincipal();
        
        try{
            
            PathNamingScheme.checkSyntax( principalString );
            principal.setType( "voms-fqan" );
            principal.setName( principalString );
            
        
        }catch(VOMSSyntaxException e){
            
            principal.setType( "x509-dn" );
            if (principalString.equals( "ANYONE" ))
                principal.setName( ANY_AUTHENTICATED_USER_DN  );
            else{ 
                
            	principal.setName( convertRFC2253toX500Subject(principalString) );
                
            }
            
        }
        
        return principal;
    }

   public static String[] permissionsFromString(String permString){
       
       String[] permissions;
       
       if (permString.contains( "|" ))
           permissions = permString.split( "\\|" );
       else 
           permissions = new String[]{permString};
       
       log.debug( "Perms: {}", StringUtils.join( permissions,"," ) );
       return permissions;
   }
    
}
