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
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class PAPManagementCLI extends ServiceCLI {

    protected static String OPT_LOCAL = "l";
    protected static String OPT_LOCAL_LONG = "local";
    protected static String OPT_LOCAL_DESCRIPTION = "Set the pap as local.";
    protected static String OPT_REMOTEL_LONG = "remote";
    protected static String OPT_REMOTE_DESCRIPTION = "Set the pap as remote.";
    protected static String OPT_NO_POLICIES_LONG = "no-policies";
    protected static String OPT_NO_POLICIES_DESCRIPTION = "Do not fetch the policies now.";
    protected PAPManagement papMgmtClient;
    protected ServiceClient serviceClient;

    public PAPManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }

    @Override
    public int executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {

        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint()
                + serviceClient.getPAPManagementServiceName());

        this.serviceClient = serviceClient;

        return executeCommand(commandLine);
    }

    protected abstract int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException;
}
