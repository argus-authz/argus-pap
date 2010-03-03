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
/**
 * 
 * An X509 principal. This class describes a PAP administrator
 * that authenticated using an X509 certificate.
 *
 */
public class X509Principal extends BasePAPAdmin {

    /**
     * The X509 distinguished name of this {@link X509Principal}
     */
    String dn;

    /**
     * Constructor
     * 
     * @param dn, the X509 dn that will be associated with this {@link X509Principal}
     */
    public X509Principal( String dn ) {

        this.dn = dn;

    }

    /**
     * Returns the name of this principal. Actually this method is a
     * Synonym for {@link #getDn()}.
     * 
     */
    public String getName() {

        return getDn();
    }

    /**
     * Returns this principal's X509 distinguished name.
     * @return
     */
    public String getDn() {

        return dn;
    }

    /**
     * Sets this principal's X509 distinguished name.
     * @param dn
     */
    public void setDn( String dn ) {

        this.dn = dn;
    }

    @Override
    public String toString() {

        return "[dn]=" + getDn();
    }

    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof X509Principal ) )
            return false;

        X509Principal that = (X509Principal) obj;

        return this.dn.equals( that.dn );

    }

    @Override
    public int hashCode() {

        if ( dn == null )
            return 1;

        return dn.hashCode();
    }

}
