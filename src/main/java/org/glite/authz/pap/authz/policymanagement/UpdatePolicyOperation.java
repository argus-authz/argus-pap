package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class UpdatePolicyOperation extends BasePAPOperation {

    PolicyType policy;
    
    private UpdatePolicyOperation(PolicyType policy) {

        this.policy = policy;
    }
    
    public static UpdatePolicyOperation instance(PolicyType policy) {

        return new UpdatePolicyOperation(policy);
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
