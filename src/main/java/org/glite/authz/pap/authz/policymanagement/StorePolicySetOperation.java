package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;


public class StorePolicySetOperation extends BasePAPOperation <String>{

    
    String idPrefix;
    PolicySetType policySet;
    
    
    private StorePolicySetOperation(String idPrefix, PolicySetType policySet) {

        this.idPrefix = idPrefix;
        this.policySet = policySet;
        
    }
    
    public static StorePolicySetOperation instance(String idPrefix, PolicySetType policySet) {

        return new StorePolicySetOperation(idPrefix,policySet);
    }
    
    protected String doExecute() {
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        String policySetId = policySet.getPolicySetId();
        
        localPAP.storePolicySet(policySet);
        
        TypeStringUtils.releaseUnusedMemory(policySet);
        
        return policySetId;
    }

    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_WRITE ) );
        
    }

}
