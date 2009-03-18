package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class ListPolicySetOperation extends BasePAPOperation<PolicySetType[]> {

    String alias;
    
    private ListPolicySetOperation(String alias) {
        this.alias = alias;
    }

    public static ListPolicySetOperation instance(String alias) {
        return new ListPolicySetOperation(alias);
    }

    @Override
    protected PolicySetType[] doExecute() {

        if (alias == null) {
            alias = PAP.DEFAULT_PAP_ALIAS;
        }
        
        PAPContainer localPAP = PAPManager.getInstance().getPAPContainer(alias);

        List<PolicySetType> policySetList = localPAP.getAllPolicySets();

        PolicySetType[] policySetArray = new PolicySetType[policySetList.size()];

        for (int i = 0; i < policySetList.size(); i++) {
            policySetArray[i] = policySetList.get(i);
        }
        
        log.info("Returning " + policySetArray.length + " policy sets");

        return policySetArray;

    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }

}
