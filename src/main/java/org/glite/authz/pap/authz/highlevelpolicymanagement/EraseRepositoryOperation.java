package org.glite.authz.pap.authz.highlevelpolicymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;

public class EraseRepositoryOperation extends BasePAPOperation<Object> {

    protected EraseRepositoryOperation() {
    }

    public static EraseRepositoryOperation instance() {
        return new EraseRepositoryOperation();
    }

    protected Object doExecute() {
        
        PAPManager papManager = PAPManager.getInstance();
        
        PAPContainer localPAPContainer = papManager.getLocalPAPContainer();
        
        localPAPContainer.deleteAllPolicySets();
        localPAPContainer.deleteAllPolicies();
        
        papManager.createLocalPAPIfNotExists();
        
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
