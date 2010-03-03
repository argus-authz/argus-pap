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
 * A PAP Access Control Entry states which permissions a specific administrator
 * has in a particular context.
 * 
 * @author andrea
 *
 */
public class PAPACE {

    /** The context for this ace **/
    PAPContext context;

    PAPAdmin admin;

    PAPPermission perms;

    private PAPACE( PAPContext context, PAPAdmin admin, PAPPermission perms ) {

        this.context = context;
        this.admin = admin;
        this.perms = perms;

    }

    public static PAPACE instance( PAPContext context, PAPAdmin admin ) {

        return new PAPACE( context, admin, null );
    }

    public static PAPACE instance( PAPContext context, PAPAdmin admin,
            PAPPermission perms ) {

        return new PAPACE( context, admin, perms );
    }

    public PAPContext getContext() {

        return context;
    }

    public void setContext( PAPContext context ) {

        this.context = context;
    }

    public PAPAdmin getAdmin() {

        return admin;
    }

    public void setAdmin( PAPAdmin admin ) {

        this.admin = admin;
    }

    public PAPPermission getPerms() {

        return perms;
    }

    public void setPerms( PAPPermission perms ) {

        this.perms = perms;
    }

}
