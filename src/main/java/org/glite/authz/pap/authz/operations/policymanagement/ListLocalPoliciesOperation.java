package org.glite.authz.pap.authz.operations.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.PAPContainer;
import org.opensaml.xacml.policy.PolicyType;

public class ListLocalPoliciesOperation extends BasePAPOperation<PolicyType[]> {

    private PAP ps;

    private ListLocalPoliciesOperation(PAP ps) {
        this.ps = ps;
    }

    public static ListLocalPoliciesOperation instance(PAP ps) {
        return new ListLocalPoliciesOperation(ps);
    }

    @Override
    protected PolicyType[] doExecute() {

        PAPContainer localPAP = new PAPContainer(ps);

        List<PolicyType> policyList = localPAP.getAllPolicies();

        PolicyType[] policyArray = new PolicyType[policyList.size()];

        for (int i = 0; i < policyList.size(); i++) {
            policyArray[i] = policyList.get(i);
        }

        log.info("Returning " + policyArray.length + " policies");

        return policyArray;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL));
    }
}
