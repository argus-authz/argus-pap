package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class ListPoliciesOperation extends BasePAPOperation <List <PolicyType>> {

    private ListPoliciesOperation() {

        
    }
    
    public static ListPoliciesOperation instance() {

        return new ListPoliciesOperation();
    }
    
    
    @Override
    protected List <PolicyType> doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        return localPAP.getAllPolicies();
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL) );
        
    }

    

}
