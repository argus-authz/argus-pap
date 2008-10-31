package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class UpdatePolicy extends BasePAPOperation {

    PolicyType policy;
    
    private UpdatePolicy(PolicyType policy) {

        this.policy = policy;
    }
    
    public static UpdatePolicy instance(PolicyType policy) {

        return new UpdatePolicy(policy);
    }
    
    @Override
    protected Object doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        localPAP.storePolicy(policy);
        
        return null;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of(PermissionFlags.POLICY_WRITE) );
        
    }

}
