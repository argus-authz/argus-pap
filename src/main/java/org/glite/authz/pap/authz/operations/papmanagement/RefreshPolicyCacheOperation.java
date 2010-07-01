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

package org.glite.authz.pap.authz.operations.papmanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;

public class RefreshPolicyCacheOperation extends BasePAPOperation<Boolean> {

    String papAlias;

    protected RefreshPolicyCacheOperation(String papAlias) {

        this.papAlias = papAlias;
    }

    public static RefreshPolicyCacheOperation instance(String papAlias) {

        return new RefreshPolicyCacheOperation(papAlias);
    }

    @Override
    protected Boolean doExecute() {

        PapManager papManager = PapManager.getInstance();

        Pap pap;

        try {
            pap = papManager.getPap(papAlias);

        } catch (NotFoundException e) {
            log.error("Unable to refresh cache, PAP not found: " + papAlias);
            return false;
        }
        
        if (!(pap.isRemote())) {
            throw new PAPException("\"" + papAlias + "\" is local, nothing to refresh");
        }

        try {
            DistributionModule.refreshCache(pap);

        } catch (Throwable t) {
            throw new PAPException(t.getMessage(), t);
        }

        return true;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_REMOTE, PermissionFlags.POLICY_WRITE));

    }

}
