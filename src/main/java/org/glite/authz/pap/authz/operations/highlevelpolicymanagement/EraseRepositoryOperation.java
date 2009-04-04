package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.PAPContainer;
import org.glite.authz.pap.papmanagement.PAPManager;

public class EraseRepositoryOperation extends BasePAPOperation<Object> {
    
    private String alias;

    protected EraseRepositoryOperation(String alias) {
        this.alias = alias;
    }

    public static EraseRepositoryOperation instance(String alias) {
        return new EraseRepositoryOperation(alias);
    }

    protected Object doExecute() {
        
        if (alias == null) {
            alias = PAP.DEFAULT_PAP_ALIAS;
        }
        
        PAPContainer papContainer = PAPManager.getInstance().getPAPContainer(alias);
        
        papContainer.deleteAllPolicySets();
        papContainer.deleteAllPolicies();
        
        papContainer.createRootPolicySet();
        
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
