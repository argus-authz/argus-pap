package org.glite.authz.pap.common.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.glite.authz.pap.common.exceptions.PAPException;

public class Utils {
    
    public static String fillWithSpaces(int n) {
        
        StringBuffer sb = new StringBuffer(n);
        
        for (int i=0; i<n; i++) {
            sb.append(' ');
        }
        
        return sb.toString();
    }
    
    public static boolean isDefined(String s) {

        if (s == null)
            return false;

        if (s.length() == 0)
            return false;

        return true;

    }
    
    public static void touchURL(String url){
       
        try {
            
            URL u = new URL(url);
            
            URLConnection conn = u.openConnection();
            
            // Ignore content...
            conn.getContent();
            
        
        } catch ( MalformedURLException e ) {
            
            throw new PAPException("Malformed URL passed as argument: "+e.getMessage(),e);
            
        } catch ( IOException e ) {
            
            throw new PAPException("Error opening URL connection: "+e.getMessage(), e);
        }
    }
}
