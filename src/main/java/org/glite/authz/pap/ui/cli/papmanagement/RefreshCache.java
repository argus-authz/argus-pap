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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.glite.authz.pap.common.Pap;

public class RefreshCache extends PAPManagementCLI {

    private static final String[] commandNameValues = { "refresh-cache", "rc" };
    private static final String DESCRIPTION = "Invalidates the local policy cache and retrieves policies "
            + "from remote paps. The arguments identify the paps that will be contacted. If no arguments are "
            + "given, all the remote paps are contacted.";
    private static final String USAGE = "[alias]...]";

    public RefreshCache() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    private List<String> getAllAliases() throws RemoteException {
        List<String> aliasList = new LinkedList<String>();

        Pap[] papArray = papMgmtClient.getAllPaps();

        for (Pap pap : papArray) {
            if (pap.isRemote()) {
                aliasList.add(pap.getAlias());
            }
        }

        return aliasList;
    }

    @Override
    protected Options defineCommandOptions() {
        return null;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws RemoteException {

        String[] args = commandLine.getArgs();
        List<String> aliasList;

        if (args.length == 1) {
            aliasList = getAllAliases();
        } else {
            aliasList = new LinkedList<String>();
            for (int i = 1; i < args.length; i++) {
                aliasList.add(args[i]);
            }
        }

        if (aliasList.isEmpty()) {
            System.out.println("No remote PAPs found.");
            return ExitStatus.SUCCESS.ordinal();
        }

        boolean success = false;
        boolean failure = false;

        for (String alias : aliasList) {
            System.out.print("Refreshing cache for pap \"" + alias + "\"...");

            try {
                boolean papExists = papMgmtClient.refreshCache(alias);
                
                if (papExists) {
                    System.out.println(" ok.");
                    success = true;
                } else {
                    System.out.println(" error: pap doesn't exist");
                    failure = true;
                }
                
            } catch (RemoteException e) {
                System.out.println("error: " + e.getMessage());
                failure = true;
                continue;
            }
        }

        if (success && failure) {
            return ExitStatus.PARTIAL_SUCCESS.ordinal();
        }

        if (!success && failure) {
            return ExitStatus.FAILURE.ordinal();
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
