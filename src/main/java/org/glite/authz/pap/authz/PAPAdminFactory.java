package org.glite.authz.pap.authz;


public class PAPAdminFactory {
    
    
    public static VOMSFQAN getFQAN(String fqan){
      
        return new VOMSFQAN(fqan);
    }
    
    public static X509Principal getDn(String dn){
        
        return new X509Principal(dn);
        
    }

}
