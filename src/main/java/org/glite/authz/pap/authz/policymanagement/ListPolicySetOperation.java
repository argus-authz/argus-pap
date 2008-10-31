package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;


public class ListPolicySetOperation extends BasePAPOperation <List <PolicySetType>> {

    
    
    private ListPolicySetOperation() {

        
    }
    
    
    public static ListPolicySetOperation instance() {
        return new ListPolicySetOperation();
    }
    
    @Override
    protected List <PolicySetType> doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        return localPAP.getAllPolicySets();
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_LOCAL ));         
    }

    
    

}
