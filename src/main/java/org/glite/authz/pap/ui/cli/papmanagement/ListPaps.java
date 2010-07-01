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
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.Pap;

public class ListPaps extends PAPManagementCLI {

    private static final String[] commandNameValues = { "list-paps", "lpaps" };
    private static final String DESCRIPTION = "List all defined paps.";
    private static final String OPT_LONGLIST_FORMAT = "l";
    private static final String OPT_LONGLIST_FORMAT_DESCRIPTION = "Use a long list format (displays all the information of a pap).";
    private static final String USAGE = "[options]";

    public ListPaps() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_LONGLIST_FORMAT_DESCRIPTION)
                                       .create(OPT_LONGLIST_FORMAT));
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        boolean logListFormat = false;

        if (commandLine.hasOption(OPT_LONGLIST_FORMAT))
            logListFormat = true;

        Pap[] papArray = papMgmtClient.getAllPaps();

        if (papArray.length == 0) {
            System.out.println("No remote PAPs has been found.");
            return ExitStatus.SUCCESS.ordinal();
        }

        for (Pap pap : papArray) {
            if (logListFormat)
                System.out.println(pap.toFormattedString());
            else {
                String visibility;
                
                if (pap.isVisibilityPublic()) {
                    visibility = "public";
                } else {
                    visibility = "private";
                }
                
                String enabledString;
                
                if (pap.isEnabled()) {
                    enabledString = "enabled";
                } else {
                    enabledString = "disabled";
                }
                
                if (pap.isLocal()) {
                    System.out.println(String.format("alias = %s (%s, %s, %s)", pap.getAlias(), pap.getTypeAsString(), enabledString, visibility));
                } else {
                    System.out.println(String.format("alias = %s (%s, %s, %s, %s)", pap.getAlias(), pap.getTypeAsString(), enabledString, visibility, pap.getEndpoint()));
                }
            }
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
