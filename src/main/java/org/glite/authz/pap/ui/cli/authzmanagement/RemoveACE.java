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

package org.glite.authz.pap.ui.cli.authzmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPPrincipal;
import org.glite.authz.pap.ui.cli.CLIException;


public class RemoveACE extends AuthZManagementCLI {
    
    private static final String USAGE = "[options] <principal>";
    private static final String[] commandNameValues = { "remove-ace", "race" };
    private static final String DESCRIPTION = "Removes an entry from the PAP global context ACL.";
    private static final String LONG_DESCRIPTION =  "<principal> can be either an X509 DN or a VOMS FQAN.";
    

    public RemoveACE() {
        
        super( commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
        
    }

    @Override
    protected int executeCommand( CommandLine commandLine )
            throws CLIException , ParseException , RemoteException {

        String[] args = commandLine.getArgs();
        
        if (args.length != 2)
            throw new ParseException("Wrong number of arguments!");
        
        PAPPrincipal principal = AuthzCLIUtils.principalFromString( args[1] );
        
        authzMgmtClient.removeACE( null, principal );
        
        return ExitStatus.SUCCESS.ordinal();
        
    }

    @Override
    protected Options defineCommandOptions() {

        // TODO Auto-generated method stub
        return null;
    }

}
