package org.glite.authz.pap.common;

import java.io.IOException;
import java.util.Properties;

import org.glite.authz.pap.common.exceptions.PAPException;

/**
 * A class representing the PAP build time version number (as derived from the maven build file)
 *  
 */
public class PAPVersion {
   
    /**
     * The singleton instance object of this class
     */
    private static PAPVersion singleton = null;
    
    /**
     * The string representation of the version number
     */
    private String version;
    
    /**
     * Costructor
     */
    private PAPVersion(){
        
        Properties versionProps = new Properties();
        try {
            
            versionProps.load( getClass().getResourceAsStream( "/pap-version.properties" ) );
            version = versionProps.getProperty( "papVersion" );
        
        } catch ( IOException e ) {
            throw new PAPException("Error loading pap version properties: "+e.getMessage(),e);
        }
        
    }
    
    /**
     * Returns the instance of the PAPVersion class.
     * 
     * @return
     */
    public static PAPVersion instance() {
        
        if (singleton == null)
            singleton = new PAPVersion();

        return singleton;
    }
    
    
    /**
     * Returns the version string for this PAP.
     * 
     * @return the string representation of the PAP build time version number, e.g. 0.9.3
     */
    public String getVersion() {

        return version;
    }

}
