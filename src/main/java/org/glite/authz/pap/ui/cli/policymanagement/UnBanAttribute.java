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
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.glite.authz.pap.ui.cli.CLIException;

public class UnBanAttribute extends PolicyManagementCLI {

    private static String[] COMMAND_NAME_VALUES = { "un-ban", "uban" };
    private static String DESCRIPTION = "Un-ban a previously banned attribute. <id> is any of the attribute ids that can be specified in the "
            + "simplified policy language. By default the attribute is un-bannen for resource and action both with value \".*\". "
            + "Different values for resource and action can be set using options --"
            + OPT_RESOURCE_LONG
            + " and --"
            + OPT_ACTION_LONG
            + ".\nExample:\npap-admin uban subject \"/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Alberto Forti\"\n"
            + "pap-admin uban fqan /dteam/test";
    private static String USAGE = "[options] <id> <value>";
    private String alias = null;

    public UnBanAttribute() {
        super(COMMAND_NAME_VALUES, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_ACTION_DESCRIPTION)
                                       .withLongOpt(OPT_ACTION_LONG)
                                       .create(OPT_ACTION));
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_RESOURCE_DESCRIPTION)
                                       .withLongOpt(OPT_RESOURCE_LONG)
                                       .create(OPT_RESOURCE));
        options.addOption(OptionBuilder.hasArg(true)
                                       .withDescription(OPT_PAPALIAS_DESCRIPTION)
                                       .withLongOpt(OPT_PAPALIAS_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
            RemoteException {

        String[] args = commandLine.getArgs();

        if (args.length != 3) {
            throw new ParseException("Wrong number of arguments");
        }

        String id = args[1];
        String value = args[2];

        if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
            alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
        }

        String resource = null;
        String action = null;

        if (commandLine.hasOption(OPT_RESOURCE)) {
            resource = commandLine.getOptionValue(OPT_RESOURCE);
        } else {
            resource = ".*";
        }

        if (commandLine.hasOption(OPT_ACTION)) {
            action = commandLine.getOptionValue(OPT_ACTION);
        } else {
            action = ".*";
        }

        if (verboseMode) {
            System.out.print("Removing ban... ");
        }

        UnbanResult unbanResult;

        unbanResult = highlevelPolicyMgmtClient.unban(alias, id, value, resource, action);

        if (unbanResult.getStatusCode() != 0) {

            System.out.println("ban policy not found.");
            return ExitStatus.FAILURE.ordinal();

        } else {
            if (verboseMode) {
                System.out.println("ok.");
            }
        }
        return ExitStatus.SUCCESS.ordinal();
    }
}
