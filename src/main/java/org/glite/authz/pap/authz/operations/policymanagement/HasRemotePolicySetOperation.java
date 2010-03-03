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

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;

public class HasRemotePolicySetOperation extends BasePAPOperation<Boolean> {

    private Pap ps;
    private String policySetId;

    protected HasRemotePolicySetOperation(Pap ps, String policySetId) {
        this.ps = ps;
        this.policySetId = policySetId;
    }

    public static HasRemotePolicySetOperation instance(Pap ps, String policySetId) {
        return new HasRemotePolicySetOperation(ps, policySetId);
    }

    @Override
    protected Boolean doExecute() {
        PapContainer localPAP = new PapContainer(ps);
        return localPAP.hasPolicySet(policySetId);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE));
    }
}
