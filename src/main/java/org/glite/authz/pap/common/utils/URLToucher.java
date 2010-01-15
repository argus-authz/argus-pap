package org.glite.authz.pap.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class URLToucher {

    public static void usage(){
        System.err.println("Usage: java -cp ... org.glite.authz.common.utils.URLToucher <url>"); 
        System.exit( 1 );
        
        
    }
    
    /**
     * @param args
     */
    public static void main( String[] args ) {

        if (args.length == 0){
            System.err.println("Please provide an URL to touch!");
            System.exit(1);
        }
        
        String urlString = args[0];
        
        try{
            
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
                       
            
            InputStream is = conn.getInputStream();
            
            do{
                is.read();
                
            }while(is.available() != 0);
                        
        } catch ( IOException e ) {
            System.err.printf( "Error contacting URL: %s -- %s\n", urlString, e.getMessage());
            
            System.exit( 1 );
        }

    }

}
