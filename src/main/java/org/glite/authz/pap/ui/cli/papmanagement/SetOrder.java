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

package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class SetOrder extends PAPManagementCLI {

    private static final String[] commandNameValues = { "set-paps-order", "spo" };
    private static final String DESCRIPTION = "Define paps ordering.";
    private static final String USAGE = "[alias]...";

    public SetOrder() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws RemoteException {

        String[] args = commandLine.getArgs();

        String[] aliasArray = null;

        int nAlias = args.length - 1;

        if (nAlias > 0) {
            aliasArray = new String[nAlias];
            for (int i = 0; i < nAlias; i++) {
                aliasArray[i] = args[i + 1];
            }
        } else {
            System.out.println("Clearing current order definition");
        }

        papMgmtClient.setOrder(aliasArray);

        return ExitStatus.SUCCESS.ordinal();
    }
}
