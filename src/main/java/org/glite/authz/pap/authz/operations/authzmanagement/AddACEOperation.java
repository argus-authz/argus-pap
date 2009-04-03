package org.glite.authz.pap.authz.operations.authzmanagement;

import org.glite.authz.pap.authz.AuthorizationEngine;
import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPACE;
import org.glite.authz.pap.authz.PAPAdmin;
import org.glite.authz.pap.authz.PAPContext;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.authz.exceptions.PAPAuthzException;

/**
 * 
 * This class implements the authorized adding of an ACE
 * to the global context PAP ACL.
 * 
 * In the current implementation the required permissions are:
 * 
 * <code>CONFIGURATION_READ,CONFIGURATION_WRITE</code>
 * 
 * 
 * @see PAPACE
 * @see PAPACL
 * @see BasePAPOperation
 * @see PAPPermission
 *
 */
public class AddACEOperation extends BasePAPOperation <Object> {

    /**
     * The ace to be added.
     */
    PAPACE ace;
    
    /**
     * Construct
     * @param ace the ace that must be added to the PAP's global ACL
     */
    private AddACEOperation(PAPACE ace) {

        this.ace = ace;
        
    }
    
    /**
     * Returns a new instance of this operation
     * @param ace the ace that must be added to the PAP's global ACL
     * @return
     */
    public static AddACEOperation instance(PAPACE ace) {

        return new AddACEOperation(ace);
    }
    
    @Override
    protected Object doExecute() {

        PAPContext ctxt = ace.getContext();
        
        if (ctxt == null)
            ctxt = AuthorizationEngine.instance().getGlobalContext();
        
        PAPAdmin admin = ace.getAdmin();
        
        if (admin == null)
            throw new PAPAuthzException("Cannot setup permissions for NULL admins!");
        
        PAPPermission perm = ace.getPerms();
        ctxt.getAcl().setPermissions( admin, perm );
        
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.CONFIGURATION_WRITE, PermissionFlags.CONFIGURATION_READ ) );
        
    }

}
