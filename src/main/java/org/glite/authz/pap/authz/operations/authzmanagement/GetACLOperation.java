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

package org.glite.authz.pap.authz.operations.authzmanagement;

import java.util.Map;

import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPACE;
import org.glite.authz.pap.authz.PAPAdmin;
import org.glite.authz.pap.authz.PAPContext;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;

/**
 * 
 * This class implements authorized access to a PAP ACL for a specific
 * PAP authorization context.
 * 
 * In the current implementation the required permissions are:
 * 
 * <code>CONFIGURATION_READ</code>
 * 
 * 
 * 
 * @see PAPContext
 * @see PAPACE
 * @see PAPACL
 * @see BasePAPOperation
 * @see PAPPermission
 *
 */
public class GetACLOperation extends BasePAPOperation <Map <PAPAdmin, PAPPermission>> {

    /**
     * The authorization context that holds the ACL
     */
    PAPContext context;
    
    
    /**
     * Constructor
     * 
     * @param context the name of the authorization context that holds the the ACL
     */
    private GetACLOperation(String context) {

        this.context = AuthorizationEngine.instance().getGlobalContext();
    }
    
    /**
     * Returns a new instance of this operation
     * @param context the name of the authorization context that holds the the ACL 
     * @return
     */
    public static GetACLOperation instance(String context) {

        return new GetACLOperation(context);
    }
    @Override
    protected Map <PAPAdmin, PAPPermission> doExecute() {

        return context.getAcl().getPermissions();
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ ) );
        
    }

}
