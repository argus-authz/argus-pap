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

import java.util.HashMap;
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
 * This class implements authorized set operation of the PAP ACL for a specific
 * PAP authorization context.
 * 
 * In the current implementation the required permissions are:
 * 
 * <code>CONFIGURATION_READ, CONFIGURATION_WRITE</code>
 * 
 * @see PAPContext
 * @see PAPACE
 * @see PAPACL
 * @see BasePAPOperation
 * @see PAPPermission
 *
 *
 */
public class SetACLOperation extends BasePAPOperation <Object> {

   
    /**
     * The context that will have the ACL set
     */
    PAPContext context;
    
    /**
     * The ACL's permissions to be set
     */
    Map<PAPAdmin, PAPPermission> permissions;
    
    
    /**
     * Constructor
     * 
     * @param context the name of the context that will have the ACL set 
     * @param permissions the ACL's permissions  
     */
    private SetACLOperation(String context, Map<PAPAdmin, PAPPermission> permissions) {
        
        this.context = AuthorizationEngine.instance().getGlobalContext();
        this.permissions = permissions;
        
    }
    
    /**
     * Returns a new instance of this operation
     * 
     * @param context the name of the context that will have the ACL set
     * @param permissions the ACL's permissions
     * @return a new instance of the {@link SetACLOperation}
     */
    public static SetACLOperation instance(String context, Map<PAPAdmin, PAPPermission> permissions) {
        return new SetACLOperation(context, permissions);
    }
    
    /**
     * Utility method used to create a copy of a map of permissions
     * 
     * @param toBeCloned the permission map to be cloned
     * @return the cloned permission map
     */
    protected Map<PAPAdmin, PAPPermission> clonePermissions(Map<PAPAdmin, PAPPermission> toBeCloned){
        
        HashMap <PAPAdmin, PAPPermission> clonedMap = (HashMap<PAPAdmin, PAPPermission>)((HashMap <PAPAdmin, PAPPermission>)toBeCloned).clone();
        return clonedMap;
    }
    
    @Override
    protected Object doExecute() {

        Map<PAPAdmin, PAPPermission> oldPerms = clonePermissions( context.getAcl().getPermissions());
        
        try{
            
            context.getAcl().getPermissions().clear();
            context.getAcl().setPermissions( permissions );
            
        }catch(Throwable t){
            
            log.error( "Error setting ACL! "+t.getMessage(), t );
            log.error( "Restoring previous permissions...");
            context.getAcl().setPermissions( oldPerms );
            
        }
        
        oldPerms.clear();
        return null;   
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_READ, PermissionFlags.CONFIGURATION_WRITE ) );
        
    }
    
    

}
