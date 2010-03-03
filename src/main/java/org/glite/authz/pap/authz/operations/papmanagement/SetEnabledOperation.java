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
import org.glite.authz.pap.papmanagement.PapManager;

public class SetEnabledOperation extends BasePAPOperation<Object> {

    private String alias;
    private boolean enabled;

    protected SetEnabledOperation(String alias, boolean enabled) {
        this.alias = alias;
        this.enabled = enabled;
    }

    public static SetEnabledOperation instance(String alias, boolean enabled) {

        return new SetEnabledOperation(alias, enabled);
    }

    @Override
    protected Object doExecute() {

        Pap pap = PapManager.getInstance().getPap(alias);

        pap.setEnabled(enabled);

        PapManager.getInstance().updatePap(pap);

        return null;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.CONFIGURATION_WRITE));
    }
}
