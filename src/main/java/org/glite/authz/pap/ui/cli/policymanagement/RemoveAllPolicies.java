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

package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RemoveAllPolicies extends PolicyManagementCLI {

    private static final String[] commandNameValues = { "remove-all-policies", "rap" };
    private static final String DESCRIPTION = "Delete all policies of a pap. Use option --" + OPT_PAPALIAS_LONG + " to specify a pap different " +
    		"than the default one.";
    private static final String USAGE = "[options]";
    
    private String alias = null;

    public RemoveAllPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(true)
                          .withDescription(OPT_PAPALIAS_DESCRIPTION)
                          .withLongOpt(OPT_PAPALIAS_LONG)
                          .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {

        if (commandLine.getArgs().length != 1) {
            throw new ParseException("Wrong number of arguments");
        }
        
        if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
        }
        
        if (verboseMode) {
            System.out.print("Removing all policies... ");
        }

        highlevelPolicyMgmtClient.eraseRepository(alias);

        if (verboseMode)
            System.out.println("ok");

        return ExitStatus.SUCCESS.ordinal();
    }
}
