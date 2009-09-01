package org.glite.authz.pap.ui.cli;

import java.io.IOException;

public class PasswordFinderImpl implements org.bouncycastle.openssl.PasswordFinder {

    private char[] password = null;
    private String prompt = "Password: ";

    public PasswordFinderImpl() {}

    public PasswordFinderImpl(String prompt) {
        this.prompt = prompt;
    }

    public char[] getPassword() {

        try {
            password = PasswordReader.getPassword(System.in, prompt);
        } catch (IOException e) {}

        return password;
    }

    public char[] getTypedPassword() {
        return password;
    }
}
