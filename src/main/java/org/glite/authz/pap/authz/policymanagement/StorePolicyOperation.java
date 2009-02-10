package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class StorePolicyOperation extends BasePAPOperation <String>{

    String idPrefix;
    PolicyType policy; 
    
    
    
    protected StorePolicyOperation(String idPrefix, PolicyType policy) {

        this.idPrefix = idPrefix;
        this.policy = policy;
    }
    
    public static StorePolicyOperation instance(String idPrefix, PolicyType policy) {

        return new StorePolicyOperation(idPrefix,policy);
    }
    
    
    protected String doExecute() {
        
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        String policyId = PolicyWizard.generateId(idPrefix);
        policy.setPolicyId(policyId);
        
        localPAP.storePolicy(policy);
        
        return policyId;
            
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_WRITE ) );
        
    }

}
