package org.glite.authz.pap.authz.operations.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.opensaml.xacml.policy.PolicyType;

public class ListRemotePoliciesOperation extends BasePAPOperation<PolicyType[]> {

    private PAP ps;

    private ListRemotePoliciesOperation(PAP ps) {
        this.ps = ps;
    }

    public static ListRemotePoliciesOperation instance(PAP ps) {
        return new ListRemotePoliciesOperation(ps);
    }

    @Override
    protected PolicyType[] doExecute() {

        PapContainer localPAP = new PapContainer(ps);

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
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
