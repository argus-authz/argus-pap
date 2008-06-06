/**************************************************************************

 Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 File : PAPConfigurationException.java

 Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.common.exceptions;

/**
 * @author Valerio Venturi <valerio.venturi@cnaf.infn.it>
 *
 */
public class PAPConfigurationException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PAPConfigurationException() {

        super();
        // TODO Auto-generated constructor stub
    }

    public PAPConfigurationException( String message, Throwable cause ) {

        super( message, cause );
        // TODO Auto-generated constructor stub
    }

    public PAPConfigurationException( String message ) {

        super( message );
        // TODO Auto-generated constructor stub
    }

    public PAPConfigurationException( Throwable cause ) {

        super( cause );
        // TODO Auto-generated constructor stub
    }

}
