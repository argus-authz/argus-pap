package org.glite.authz.pap.authz;


public class PAPAdminFactory {
    
    public static final String ANY_AUTHENTICATED_USER_DN = "/O=PAP/OU=Internal/CN=Any authenticated user";
    
    public static final X509Principal AnyAuthenticatedUserAdmin = getDn( ANY_AUTHENTICATED_USER_DN );
    
    
    public static VOMSFQAN getFQAN(String fqan){
      
        return new VOMSFQAN(fqan);
    }
    
    public static X509Principal getDn(String dn){
        
        return new X509Principal(dn);
        
    }
    
    public static X509Principal getAnyAuthenticatedUserAdmin(){
        
        return AnyAuthenticatedUserAdmin;
    }

}
