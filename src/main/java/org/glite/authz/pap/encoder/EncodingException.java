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

package org.glite.authz.pap.encoder;

/**
 * This class is the exception raised by
 * {@link org.glite.authz.pap.encoder.PolicyFileEncoder}
 * when there are parsing problems.
 *
 * It acts as an adaptor of other errors.
 *
 * @author Vincenzo Ciaschini
 */
public class EncodingException extends Exception {
    /**
     * This method converts a {@code Throwable} object to
     * an EncodingException object.
     *
     * @param cause The {@code Throwable} object to convert.
     */
    public EncodingException(Throwable cause) {
        super(cause);
    }

    /**
     * This method creates a basic exception.
     */
    public EncodingException() {
        super();
    }

    /**
     * This method raises an EncodingException object with a specified message.
     *
     * @param message The message to add.
     */
    public EncodingException(String message) {
        super(message);
    }

    /**
     * This method raises an EncodingException object with a specified message
     * and to encapsulate the specified Throwable object.
     *
     * @param cause The {@code Throwable} object to convert.
     * @param message The message to add.
     */
    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
