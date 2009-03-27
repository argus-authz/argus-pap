package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class ListLocalPolicySetOperation extends BasePAPOperation<PolicySetType[]> {

    private PAP ps;

    private ListLocalPolicySetOperation(PAP ps) {
        this.ps = ps;
    }

    public static ListLocalPolicySetOperation instance(PAP ps) {
        return new ListLocalPolicySetOperation(ps);
    }

    @Override
    protected PolicySetType[] doExecute() {

        PAPContainer localPAP = new PAPContainer(ps);

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
