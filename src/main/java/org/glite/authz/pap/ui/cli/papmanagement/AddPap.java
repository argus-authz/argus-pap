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

public class AddPap extends PAPManagementCLI {

    private static final String[] commandNameValues = { "add-pap", "apap" };
    private static final String DESCRIPTION = "Add a remote or local pap.\n"
            + "<alias> is a friendly name (it has to be unique) used to identify the pap\n"
            + "<endpoint> endpoint of the PAP in the following format: [<protocol>://]<host>[:<port>[/path]]\n"
            + "<dn> DN of the endpoint machine";
    private static final String LONG_DESCRIPTION = "A new added pap is disabled by default (i.e. its policies are not "
            + "sent to the PDP). Use the command \"enable-pap\" to enable a pap. Policies are fetched "
            + "immediately unless option --"
            + OPT_NO_POLICIES_LONG
            + " is given. \nDefault protocol is: "
            + Pap.DEFAULT_PROTOCOL
            + "\nDefault port is: "
            + Pap.DEFAULT_PORT
            + "\nDefault path is: "
            + Pap.DEFAULT_SERVICES_ROOT_PATH
            + "\nExample:\n\t pap-admin "
            + commandNameValues[0]
            + " cnaf_pap test.cnaf.infn.it \"/C=IT/O=INFN/OU=Host/L=CNAF/CN=test.cnaf.infn.it\"";
    private static final String LOPT_PRIVATE = "private";
    private static final String LOPT_PUBLIC = "public";
    private static final String USAGE = "[options] <alias> [<endpoint> <dn>]";

    public AddPap() {
        super(commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription("Set the pap as public (allow to distribute its policies)")
                                       .withLongOpt(LOPT_PUBLIC)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription("Set the pap as private (default)")
                                       .withLongOpt(LOPT_PRIVATE)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_LOCAL_DESCRIPTION)
                                       .withLongOpt(OPT_LOCAL_LONG)
                                       .create(OPT_LOCAL));
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_REMOTE_DESCRIPTION)
                                       .withLongOpt(OPT_REMOTEL_LONG)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_NO_POLICIES_DESCRIPTION)
                                       .withLongOpt(OPT_NO_POLICIES_LONG)
                                       .create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if ((args.length != 4) && (args.length != 2)) {
            throw new ParseException("Wrong number of arguments");
        }

        boolean isLocal;
        boolean isPublic = false;

        if (args.length != 2) {
            isLocal = false;
        } else {
            isLocal = true;
        }

        if (commandLine.hasOption(OPT_LOCAL)) {
            isLocal = true;
        }

        if (commandLine.hasOption(OPT_REMOTEL_LONG)) {
            isLocal = false;
        }

        if (commandLine.hasOption(LOPT_PUBLIC)) {
            isPublic = true;
        }

        String alias = args[1];
        String protocol = null;
        String dn = null;
        String host = null;
        String port = null;
        String path = null;

        if (args.length != 2) {
            protocol = getProtocol(args[2]);
            host = getHostname(args[2]);
            port = getPort(args[2]);

            validatePort(port);
            
            path = getPath(args[2]);
            dn = args[3];
        }

        Pap pap = new Pap(alias, isLocal, dn, host, port, path, protocol, isPublic);

        String msg = "Adding pap: ";

        if (verboseMode) {
            System.out.println(msg + pap.toFormattedString(0, msg.length()));
        }

        if (papMgmtClient.exists(pap.getAlias())) {
            System.out.println("pap already exists.");
            return ExitStatus.FAILURE.ordinal();
        }

        papMgmtClient.addPap(pap);

        if (pap.isRemote()) {

            if (!commandLine.hasOption(OPT_NO_POLICIES_LONG)) {

                if (verboseMode) {
                    System.out.print("Retrieving policies... ");
                }

                try {
                    papMgmtClient.refreshCache(pap.getAlias());
                } catch (RemoteException e) {
                    System.out.println("Error: pap successfully added but cannot retrieve policies.");
                    throw e;
                }

                if (verboseMode) {
                    System.out.println("ok.");
                }
            }
        }

        return ExitStatus.SUCCESS.ordinal();
    }

    protected static String getHostname(String endpoint) {

        int start = endpoint.indexOf("://");

        if (start == -1) {
            start = 0;
        } else {
            start += 3;
        }

        int end = endpoint.indexOf(':', start);

        if (end == -1) {
            end = endpoint.length();
        }

        String hostname = endpoint.substring(start, end);
        
        if (hostname.length() == 0) {
            return null;
        }
        return hostname;
    }

    protected static String getPath(String endpoint) {
        int start = endpoint.indexOf("://");

        if (start == -1) {
            start = 0;
        } else {
            start += 3;
        }

        start = endpoint.indexOf('/', start);

        if (start == -1) {
            return null;
        }

        int end = endpoint.length();

        return endpoint.substring(start, end);
    }

    protected static String getPort(String endpoint) {
        int start = endpoint.indexOf("://");

        if (start == -1) {
            start = 0;
        } else {
            start += 3;
        }

        start = endpoint.indexOf(':', start);

        int end = endpoint.indexOf('/', start);

        if (start == -1) {
            return null;
        }

        start++; // skip the ':'

        if (end == -1) {
            end = endpoint.length();
        }

        return endpoint.substring(start, end);
    }

    protected static String getProtocol(String endpoint) {

        int index = endpoint.indexOf("://");

        if (index == -1) {
            return null;
        }
        String protocol = endpoint.substring(0, index);
        
        if (protocol.length() == 0) {
            return null;
        }
        return protocol;
    }
    
    protected static void validatePort(String port) throws ParseException {
        
        if (port != null) {
            
            int portNum;
            
            try {
                
                portNum = Integer.parseInt(port);
                
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid port number: " + port);
            }
            
            if (portNum > 65535) {
                throw new ParseException("Invalid port number: " + port);
            }
        }
    }
}
