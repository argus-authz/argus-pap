package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicySetType;


public class GetLocalPolicySetOperation extends BasePAPOperation <PolicySetType>{

    private Pap ps;
    private String policySetId;
    
    protected GetLocalPolicySetOperation( Pap ps, String policySetId ) {
        this.ps = ps;
        this.policySetId = policySetId;
    }

    public static GetLocalPolicySetOperation instance(Pap ps, String policySetId) {
        return new GetLocalPolicySetOperation(ps, policySetId);
    }
    
    @Override
    protected PolicySetType doExecute() {
    	
        PapContainer papContainer = new PapContainer(ps);
        
        if (!papContainer.hasPolicySet(policySetId))
            throw new NotFoundException("PolicySet '" + policySetId + "' not found.");
        
        PolicySetType policySet = papContainer.getPolicySet(policySetId);
        
        return policySet;
        
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_LOCAL ) );
    }
}