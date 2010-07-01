/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.authz.operations.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.opensaml.xacml.policy.PolicyType;

public class ListRemotePoliciesOperation extends BasePAPOperation<PolicyType[]> {

    private Pap ps;

    private ListRemotePoliciesOperation(Pap ps) {
        this.ps = ps;
    }

    public static ListRemotePoliciesOperation instance(Pap ps) {
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
