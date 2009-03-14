package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class UpdatePolicyOperation extends BasePAPOperation <Boolean> {

	String version;
    PolicyType policy;
    
    private UpdatePolicyOperation(String version, PolicyType policy) {

    	this.version = version;
        this.policy = policy;
    }
    
    public static UpdatePolicyOperation instance(String version, PolicyType policy) {

        return new UpdatePolicyOperation(version, policy);
    }
    
    @Override
    protected Boolean doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        if (!(localPAP.hasPolicy(policy.getPolicyId()))) {
            return false;
        }
        
        localPAP.updatePolicy(version, policy);
        
        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of(PermissionFlags.POLICY_WRITE) );
        
    }

}
