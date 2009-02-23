package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class ListPoliciesForPAPOperation extends
        BasePAPOperation <PolicyType[]>
{
    
    String papAlias;
    
    
    protected ListPoliciesForPAPOperation(String papAlias) {

        this.papAlias = papAlias;
        
    }
    
    public static ListPoliciesForPAPOperation instance(String papAlias) {

        return new ListPoliciesForPAPOperation(papAlias);
    }

    @Override
    protected PolicyType[] doExecute() {

        PAPContainer papContainer = PAPManager.getInstance().getRemotePAPContainer(papAlias);
        
        List<PolicyType> policyList = papContainer.getAllPolicies();
        PolicyType[] policyArray = new PolicyType[policyList.size()];
        
        for (int i=0; i<policyList.size(); i++) {
            policyArray[i] = policyList.get(i);
        }
        
        return policyArray;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_REMOTE ) );
        
    }

    

}
