package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.policy.PolicySetType;


public class GetPolicySetOperation extends BasePAPOperation <PolicySetType>{

    String policySetId;
    
     
    protected GetPolicySetOperation( String policySetId ) {

        this.policySetId = policySetId;
    
    }

    public static GetPolicySetOperation instance(String policySetId) {

        return new GetPolicySetOperation(policySetId);
    }
    
    @Override
    protected PolicySetType doExecute() {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        if (!localPAP.hasPolicySet(policySetId))
            throw new NotFoundException("PolicySet '" + policySetId + "' not found.");
        
        PolicySetType policySet = localPAP.getPolicySet(policySetId);
        
        return policySet;
        
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission( PAPPermission.of( PermissionFlags.POLICY_READ_LOCAL ) );
        
    }
    
    

}
