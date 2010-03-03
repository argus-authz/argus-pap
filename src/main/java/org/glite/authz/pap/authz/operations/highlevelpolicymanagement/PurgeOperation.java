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

package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.HighLevelPolicyManagementServiceException;

public class PurgeOperation extends BasePAPOperation<Object> {

    private String alias;
    private boolean purgeUnreferencedPolicies;
    private boolean purgeEmptyPolicies;
    private boolean purgeUnreferencedPolicySets;
    private boolean purgeEmptyPolicySets;

    protected PurgeOperation(String alias, boolean purgeUnreferencedPolicies, boolean purgeEmptyPolicies,
            boolean purgeUnreferencedPolicySets, boolean purgeEmptyPolicySets) {
        this.alias = alias;
        this.purgeUnreferencedPolicies = purgeUnreferencedPolicies;
        this.purgeEmptyPolicies = purgeEmptyPolicies;
        this.purgeUnreferencedPolicySets = purgeUnreferencedPolicySets;
        this.purgeEmptyPolicySets = purgeEmptyPolicySets;
    }

    public static PurgeOperation instance(String alias, boolean purgeUnreferencedPolicies,
            boolean purgeEmptyPolicies, boolean purgeUnreferencedPolicySets, boolean purgeEmptyPolicySets) {
        return new PurgeOperation(alias,
                                  purgeUnreferencedPolicies,
                                  purgeEmptyPolicies,
                                  purgeUnreferencedPolicySets,
                                  purgeEmptyPolicySets);
    }

    protected Object doExecute() {

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }

        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new HighLevelPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        if (purgeUnreferencedPolicies) {
            papContainer.purgeUnreferencesPolicies();
        }

        if (purgeEmptyPolicies) {
            papContainer.purgePoliciesWithNoRules();
        }

        if (purgeUnreferencedPolicySets) {
            papContainer.purgeUnreferencedPolicySets();
        }

        if (purgeEmptyPolicySets) {
            papContainer.purgePolicySetWithNoPolicies();
        }
        return null;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE,
                                               PermissionFlags.POLICY_READ_LOCAL));
    }
}
