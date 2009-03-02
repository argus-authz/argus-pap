package org.glite.authz.pap.common.utils;

public class Utils {
    
    public static String fillWithSpaces(int n) {
        
        StringBuffer sb = new StringBuffer(n);
        
        for (int i=0; i<n; i++) {
            sb.append(' ');
        }
        
        return sb.toString();
    }

}
