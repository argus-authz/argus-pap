package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;


public class ListPolicySetsForPAPOperation extends
        BasePAPOperation <PolicySetType[]>{
    
    String papId;
    
    private ListPolicySetsForPAPOperation(String papId) {

        this.papId = papId;
    }
    
    public static ListPolicySetsForPAPOperation instance(String papId) {

        return new ListPolicySetsForPAPOperation(papId);
    }

    
    @Override
    protected PolicySetType[] doExecute() {
        
        PAPContainer pap = PAPManager.getInstance().getPAPContainer( papId );
        
        List<PolicySetType> policySetList = pap.getAllPolicySets();
        
        PolicySetType[] policySetArray = new PolicySetType[policySetList.size()];
        
        for (int i=0; i<policySetList.size(); i++) {
            policySetArray[i] = policySetList.get(i);
        }
        
        return policySetArray;
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_REMOTE ) );
        
    }

    

}
