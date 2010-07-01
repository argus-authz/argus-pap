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

package org.glite.authz.pap.authz;

import org.bouncycastle.voms.VOMSAttribute.FQAN;
import org.glite.authz.pap.authz.util.DNImpl;
import org.glite.security.util.DN;

/**
 * Creates {@link PAPAdmin} objects currently supported 
 * by this PAP implementation. 
 *
 */
public class PAPAdminFactory {

    /** The string used internally to denote any X509 authenticated user **/
    public static final String ANY_AUTHENTICATED_USER_DN = "/O=PAP/OU=Internal/CN=Any authenticated user";

    /** The {@link X509Principal} admin that describes any authenticated user admin **/
    public static final X509Principal AnyAuthenticatedUserAdmin = getDn( ANY_AUTHENTICATED_USER_DN );

    /** Creats a new {@link VOMSFQAN} admin starting from an FQAN string.
     *  
     *  @param fqan, the VOMS {@link FQAN} string
     *  @return the {@link VOMSFQAN} principal for the given fqan
     * **/
    public static VOMSFQAN getFQAN( String fqan ) {

        fqan = fqan.replaceAll( "\\/Role=NULL", "" );
        fqan = fqan.replaceAll( "\\/Capability=NULL", "" );
        
        return new VOMSFQAN( fqan );
    }

    /**
     * Creates a new {@link X509Principal} admin starting from a string encoded X509 distinguished name (DN).
     * @param dn, the X509 certificate distinguished name
     * 
     * @return the {@link X509Principal} principal for the given dn
     */
    public static X509Principal getDn( String dn ) {

        DN theDN = new DNImpl(dn);
        
        return new X509Principal( theDN.getX500() );

    }

    /**
     * Returns the {@link X509Principal} admin corresponding to any authenticated user.
     * @return the {@link X509Principal} principal for any authenticated user
     */
    public static X509Principal getAnyAuthenticatedUserAdmin() {

        return AnyAuthenticatedUserAdmin;
    }

}
