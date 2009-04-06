package org.glite.authz.pap.authz.operations.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class ListLocalPolicySetOperation extends BasePAPOperation<PolicySetType[]> {

    private Pap ps;

    private ListLocalPolicySetOperation(Pap ps) {
        this.ps = ps;
    }

    public static ListLocalPolicySetOperation instance(Pap ps) {
        return new ListLocalPolicySetOperation(ps);
    }

    @Override
    protected PolicySetType[] doExecute() {

        PapContainer localPAP = new PapContainer(ps);

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
