package org.glite.authz.pap.ui.cli;

import java.io.IOException;


public class PasswordFinderImpl implements org.bouncycastle.openssl.PasswordFinder {

    private static char[] password = null;
    
    public char[] getPassword() {
        
        try {
            password = PasswordReader.getPassword(System.in, "Password: ");
        } catch (IOException e) {
        }
        
        return password;
    }
    
    public static char[] getTypedPassword() {
        return password;
    }
}
