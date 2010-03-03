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
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.ui.cli.CLIException;

public class Ping extends PAPManagementCLI {

    private static final String[] commandNameValues = { "ping" };
    private static final String DESCRIPTION = "Ping a PAP (default endpoint is: "
            + String.format(DEFAULT_SERVICE_URL,
                            Pap.DEFAULT_HOST,
                            Pap.DEFAULT_PORT,
                            Pap.DEFAULT_SERVICES_ROOT_PATH) + ").";
    private static final String USAGE = "";

    public Ping() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length > 1)
            throw new ParseException("Wrong number of arguments");
        
        System.out.print("Contacting PAP at \"" + serviceClient.getTargetEndpoint() + "\"... ");

        String papVersion = papMgmtClient.ping();

        System.out.println("ok (" + papVersion + ")");

        return ExitStatus.SUCCESS.ordinal();
    }
}
