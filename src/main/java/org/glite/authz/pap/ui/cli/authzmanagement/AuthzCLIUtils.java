/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.ui.cli.authzmanagement;

import org.apache.commons.lang.StringUtils;
import org.glite.authz.pap.authz.util.DNImpl;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.common.exceptions.VOMSSyntaxException;
import org.glite.authz.pap.common.utils.PathNamingScheme;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPPrincipal;
import org.glite.security.util.DN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthzCLIUtils {
    
    public static final String ANY_AUTHENTICATED_USER_DN = "/O=PAP/OU=Internal/CN=Any authenticated user";
    public static final Logger log = LoggerFactory.getLogger( AuthzCLIUtils.class );
    
    public static String convertX500SubjectToRFC2253(String x500Subject){
    	
    	try{	
    	
    		DN dn = new DNImpl(x500Subject);
    		return dn.getRFC2253();
    		
    	}catch(IllegalArgumentException e){
    		
    		throw new PAPException("The string passed as argument is not a valid certificate subject!", e);
    	}
    }
    
    
    public static String convertRFC2253toX500Subject(String rfc2253Subject){
    	try{
    		
    		DN dn = new DNImpl(rfc2253Subject);
    		return dn.getX500();
    		
    	}catch(IllegalArgumentException e){
    		
    		throw new PAPException("The string passed as argument is not a valid certificate subject!", e);
    	}
    	
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
