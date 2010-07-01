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
