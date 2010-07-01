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
 * A PAP authorization context. Each authorization context
 * has an associated {@link ACL} describing who is allowed
 * to do certain operations in such context.
 */
public class PAPContext {

    /** The name of this context **/
    String name;

    /** The ACL of this context **/
    ACL acl;

    /**
     * Constructor.
     * @param name, the name for this context
     */
    private PAPContext( String name ) {

        this.name = name;
        acl = new ACL();

    }

    /**
     * Returns the name for this context
     * @return
     */
    public String getName() {

        return name;
    }

    /**
     * Sets the name for this context
     * @param name
     */
    public void setName( String name ) {

        this.name = name;
    }

    /**
     * Returns the ACL for this context
     * @return
     */
    public ACL getAcl() {

        return acl;
    }

    /**
     * Sets the ACL for this context
     * @param acl
     */
    public void setAcl( ACL acl ) {

        this.acl = acl;
    }

    /**
     * Creates a context with the name passed as argument
     * 
     * @param name
     * @return
     */
    public static PAPContext instance( String name ) {

        return new PAPContext( name );
    }

    @Override
    /** **/
    public String toString() {
    
        return name;
    }
}
