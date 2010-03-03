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
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;

/**
 * PasswordReader reads a password and masks the typed inputs.
 * 
 * See article: Password Masking in the Java Programming Language
 * http://java.sun.com/developer/technicalArticles/Security/pwordmask/
 * 
 * Usage:
 * 
 * <pre>
 * char[] password = PasswordReader.getPassword(System.in, &quot;Password: &quot;);
 * </pre>
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * 
 */
public class PasswordReader implements Runnable {

    /**
     * 
     * @param in
     * @param prompt
     * @return
     * @throws IOException
     */
    public static final char[] getPassword(InputStream in, String prompt) throws IOException {
        PasswordReader maskingThread = new PasswordReader(prompt);
        Thread thread = new Thread(maskingThread);
        thread.start();

        char[] lineBuffer;
        char[] buf;

        buf = lineBuffer = new char[128];

        int room = buf.length;
        int offset = 0;
        int c;

        loop: while (true) {
            switch (c = in.read()) {
            case -1:
            case '\n':
                break loop;

            case '\r':
                int c2 = in.read();
                if ((c2 != '\n') && (c2 != -1)) {
                    if (!(in instanceof PushbackInputStream)) {
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream) in).unread(c2);
                } else {
                    break loop;
                }

            default:
                if (--room < 0) {
                    buf = new char[offset + 128];
                    room = buf.length - offset - 1;
                    System.arraycopy(lineBuffer, 0, buf, 0, offset);
                    Arrays.fill(lineBuffer, ' ');
                    lineBuffer = buf;
                }
                buf[offset++] = (char) c;
                break;
            }
        }
        maskingThread.stopMasking();
        if (offset == 0) {
            return null;
        }
        char[] ret = new char[offset];
        System.arraycopy(buf, 0, ret, 0, offset);
        Arrays.fill(buf, ' ');
        return ret;
    }

    private volatile boolean masking;
    private String prompt;

    /**
     * @param prompt The prompt displayed to the user
     */
    private PasswordReader(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Begin masking until asked to stop.
     */
    public void run() {

        int priority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        try {
            masking = true;
            while (masking) {
                System.out.print("\010 \015" + prompt);
                try {
                    // attempt masking at this rate
                    Thread.sleep(1);
                } catch (InterruptedException iex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } finally { // restore the original priority
            Thread.currentThread().setPriority(priority);
        }
    }

    /**
     * Instruct the thread to stop masking.
     */
    private void stopMasking() {
        this.masking = false;
    }

    /**
     * Test drive
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            char[] password = PasswordReader.getPassword(System.in, "Password: ");
            if (password != null) {
                System.out.println("Password entered: " + String.valueOf(password));
            } else {
                System.out.println("Empty password entered.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
