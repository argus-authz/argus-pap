package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;


public class ListPoliciesOperation extends BasePAPOperation <PolicyType[]> {

    String alias;
    
    private ListPoliciesOperation(String alias) {    
        this.alias = alias;
    }
    
    public static ListPoliciesOperation instance(String alias) {
        return new ListPoliciesOperation(alias);
    }
    
    
    @Override
    protected PolicyType[] doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getPAPContainer(alias);
        
        List<PolicyType> policyList = localPAP.getAllPolicies();
        
        PolicyType[] policyArray = new PolicyType[policyList.size()];
        
        for (int i=0; i<policyList.size(); i++) {
            policyArray[i] = policyList.get(i);
        }
        
        log.info("Returning " + policyArray.length + " policies");
        
        return policyArray;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL) );
        
    }

    

}
