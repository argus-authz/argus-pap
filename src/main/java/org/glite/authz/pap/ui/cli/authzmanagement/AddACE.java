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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddACE extends AuthZManagementCLI {
    
    @SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger( AddACE.class );
    
    private static final String USAGE = "[options] <principal> <permissions>";
    private static final String[] commandNameValues = { "add-ace", "aace" };
    private static final String DESCRIPTION = "Adds an entry to the ACL for the PAP global context.";
    private static final String LONG_DESCRIPTION =  "<principal> can be either an X509 DN or a VOMS FQAN. ANYONE can be used to assign permissions to any authenticated user." +
    		"\n<permissions> is a | separated list of PAP permissions that will be assigned to <principal>." +
    		"\nThe ALL shortcut can be used to assign all permission to a principal." +
    		"\n\nExample:\n" +
    		"\t pap add-ace '/atlas/Role=VO-Admin' 'ALL'";
    		

    
    public AddACE() {

        super( commandNameValues, USAGE,DESCRIPTION, LONG_DESCRIPTION);
        
    }

    @Override
    protected int executeCommand( CommandLine commandLine )
            throws CLIException , ParseException , RemoteException {
        
        String[] args = commandLine.getArgs();
        
        if (args.length != 3)
            throw new ParseException("Wrong number of arguments!");
        
        String principalString = args[1];
        String permString = args[2];
        
        PAPPrincipal principal = AuthzCLIUtils.principalFromString( principalString );
        String[] permissions = AuthzCLIUtils.permissionsFromString( permString );
        
        authzMgmtClient.addACE( null, principal, permissions );
        
        return ExitStatus.SUCCESS.ordinal();

    }

    @Override
    protected Options defineCommandOptions() {

        return null;
    }

}
