package org.glite.authz.pap.authz;


public class PAPContext {

    String name;
    ACL acl;
    
    public String getName() {
    
        return name;
    }
    
    public void setName( String name ) {
    
        this.name = name;
    }
    
    public ACL getAcl() {
    
        return acl;
    }
    
    public void setAcl( ACL acl ) {
    
        this.acl = acl;
    }
    
    
}
