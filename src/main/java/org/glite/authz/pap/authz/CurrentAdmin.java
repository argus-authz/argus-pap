package org.glite.authz.pap.authz;

import org.slf4j.Logger;


public class CurrentAdmin {

    Logger log;
    
    private PAPAdmin papAdmin;
    
    
    protected CurrentAdmin( PAPAdmin admin) {

        this.papAdmin = admin;
    }
    
    
    public static CurrentAdmin instance(){
        
        return null;
    }
    
    
}
