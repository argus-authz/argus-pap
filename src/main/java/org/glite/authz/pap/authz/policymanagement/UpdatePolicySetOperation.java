package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;


public class UpdatePolicySetOperation extends BasePAPOperation {

    PolicySetType policySet;
    
    public UpdatePolicySetOperation(PolicySetType policySet) {

        this.policySet = policySet;
    }
    
    public static UpdatePolicySetOperation instance(PolicySetType policySet) {
        return new UpdatePolicySetOperation(policySet);
    }
    
    @Override
    protected Object doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        localPAP.storePolicySet(policySet);
        return null;
    }
    
    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of(PermissionFlags.POLICY_WRITE) );
        
    }

}
