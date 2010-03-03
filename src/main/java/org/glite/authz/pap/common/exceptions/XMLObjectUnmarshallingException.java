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

package org.glite.authz.pap.common.exceptions;

public class XMLObjectUnmarshallingException extends XMLObjectException {

    private static final long serialVersionUID = -7268709514539060454L;

    public XMLObjectUnmarshallingException() {
    }

    public XMLObjectUnmarshallingException(String message) {
	super(message);
    }

    public XMLObjectUnmarshallingException(Throwable cause) {
	super(cause);
    }

    public XMLObjectUnmarshallingException(String message, Throwable cause) {
	super(message, cause);
    }

}
