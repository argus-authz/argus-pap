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
import org.apache.commons.lang.StringUtils;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPACE;
import org.glite.authz.pap.ui.cli.CLIException;


public class ListACL extends AuthZManagementCLI {
    
    private static final String USAGE = "[options] <context>";
    private static final String[] commandNameValues = { "list-acl", "lacl" };
    private static final String DESCRIPTION = "Lists the ACL for a given context..";
    private static final String LONG_DESCRIPTION = "Lists the ACL for a given PAP authz context. Currently only " +
    		"the context 'global-context' is supported. If no context is passed as argument the default one will " +
    		"be used.";
    private static final String ANY_AUTHENTICATED_USER_DN = "/O=PAP/OU=Internal/CN=Any authenticated user";
    
    public ListACL() {
        super( commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
    }

    @Override
    protected int executeCommand( CommandLine commandLine )
            throws CLIException , ParseException , RemoteException {

        PAPACE[] aces = authzMgmtClient.getACL( null );
        
        if (aces.length == 0)
            System.out.println("ACL is empty!");
        
        for (PAPACE ace: aces)  
            printACE( ace );
        
        return ExitStatus.SUCCESS.ordinal();

    }
    
    protected void printACE(PAPACE ace){
        String formatString; 
        
        String principalName = null;
        
        if (ace.getPrincipal().getType().equals( "x509-dn" )){
        	if (ace.getPrincipal().getName().equals(ANY_AUTHENTICATED_USER_DN)){
        		principalName = "ANYONE";
        		formatString = "\n%s :\n\t%s\n";
        		
        	}else{
        		principalName = ace.getPrincipal().getName();	
        		formatString = "\n\"%s\" :\n\t%s\n";
        	}
        }else{
        	principalName = ace.getPrincipal().getName();
        	formatString = "\n%s :\n\t%s\n";
        }
            
              	
        
        System.out.format(formatString, principalName, StringUtils.join( ace.getPermissions(),"|" ));
        
    }
    @Override
    protected Options defineCommandOptions() {

        return null;
    }

}
