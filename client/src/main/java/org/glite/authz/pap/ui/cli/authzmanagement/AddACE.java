package org.glite.authz.pap.ui.cli.authzmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.services.authz_management.axis_skeletons.PAPPrincipal;
import org.glite.authz.pap.ui.cli.CLIException;


public class AddACE extends AuthZManagementCLI {
    
    private static final String USAGE = "<principal> <permissions> [options]";
    private static final String[] commandNameValues = { "add-ace", "aace" };
    private static final String DESCRIPTION = "Adds an entry to the ACL for the PAP global context.";
    private static final String LONG_DESCRIPTION =  "<principal> can be either an X509 DN or a VOMS FQAN." +
    		"\n<permissions> is a | separated list of PAP permissions that will be assigned to <principal>." +
    		"\nThe ALL shortcut can be used to assign all permission to a principal." +
    		"\n\nExample:\n" +
    		"\t pap add-ace '/atlas/Role=VO-Admin' 'ALL'";
    		

    
    public AddACE() {

        super( commandNameValues, USAGE,DESCRIPTION, LONG_DESCRIPTION);
        
    }

    @Override
    protected void executeCommand( CommandLine commandLine )
            throws CLIException , ParseException , RemoteException {
        
        String[] args = commandLine.getArgs();
        
        if (args.length != 3)
            throw new ParseException("Wrong number of arguments!");
        
        String principalString = args[1];
        String permString = args[2];
        
        PAPPrincipal principal = AuthzCLIUtils.principalFromString( principalString );
        String[] permissions = AuthzCLIUtils.permissionsFromString( permString );
        
        authzMgmtClient.addACE( null, principal, permissions );
        
        return;

    }

    @Override
    protected Options defineCommandOptions() {

        return null;
    }

}
