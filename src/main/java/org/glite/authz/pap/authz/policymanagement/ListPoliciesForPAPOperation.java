package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class ListPoliciesForPAPOperation extends
        BasePAPOperation <PolicyType[]>
{
    
    String papId;
    
    
    protected ListPoliciesForPAPOperation(String papId) {

        this.papId = papId;
        
    }
    
    public static ListPoliciesForPAPOperation instance(String papId) {

        return new ListPoliciesForPAPOperation(papId);
    }

    @Override
    protected PolicyType[] doExecute() {

        PAPContainer pap = PAPManager.getInstance().getTrustedPAPContainer(papId);
        return (PolicyType[]) pap.getAllPolicies().toArray();
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_REMOTE ) );
        
    }

    

}
