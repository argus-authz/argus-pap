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
import org.apache.commons.cli.ParseException;

public class GetOrder extends PAPManagementCLI {

    private static final String[] commandNameValues = { "get-paps-order", "gpo" };
    private static final String DESCRIPTION = "Get paps ordering.";
    private static final String USAGE = "";

    public GetOrder() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        String[] aliasArray = papMgmtClient.getOrder();

        if (aliasArray == null) {
            System.out.println("No ordering has been defined");
            return ExitStatus.SUCCESS.ordinal();
        }

        if (aliasArray.length == 0) {
            System.out.println("No ordering has been defined");
            return ExitStatus.SUCCESS.ordinal();
        }

        System.out.print(aliasArray[0]);

        for (int i = 1; i < aliasArray.length; i++) {
            System.out.print(", " + aliasArray[i]);
        }

        System.out.println("");

        return ExitStatus.SUCCESS.ordinal();

    }
}
