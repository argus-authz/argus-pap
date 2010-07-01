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
