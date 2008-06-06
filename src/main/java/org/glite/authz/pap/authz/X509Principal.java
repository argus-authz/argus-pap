package org.glite.authz.pap.authz;


public class X509Principal extends BasePAPAdmin {

    String dn;
    
    public X509Principal(String dn) {

        this.dn = dn;
        
    }
    
    public String getName() {

        return getDn();
    }

    
    public String getDn() {
    
        return dn;
    }

    
    public void setDn( String dn ) {
    
        this.dn = dn;
    }
    
    @Override
    public String toString() {
    
        return "[dn]="+getDn();
    }
    
    @Override
    public boolean equals( Object obj ) {
    
        if (! (obj instanceof X509Principal))
            return false;
        
        X509Principal that = (X509Principal)obj;
        
        return this.dn.equals( that.dn );
        
    }
    
    @Override
    public int hashCode() {
    
        if (dn == null)
            return 1;
        
        return dn.hashCode();
    }

}
