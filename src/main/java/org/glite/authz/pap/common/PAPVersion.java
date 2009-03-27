package org.glite.authz.pap.common;

import java.io.IOException;
import java.util.Properties;

import org.glite.authz.pap.common.exceptions.PAPException;


public class PAPVersion {
    
    private static PAPVersion singleton = null;
    
    private String version;
    
    private PAPVersion(){
        
        Properties versionProps = new Properties();
        try {
            
            versionProps.load( getClass().getResourceAsStream( "/pap-version.properties" ) );
            version = versionProps.getProperty( "papVersion" );
        
        } catch ( IOException e ) {
            throw new PAPException("Error loading pap version properties: "+e.getMessage(),e);
        }
        
    }
    public static PAPVersion instance() {
        
        if (singleton == null)
            singleton = new PAPVersion();

        return singleton;
    }
    
    
    public String getVersion() {

        return version;
    }

}
