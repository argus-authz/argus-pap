package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.CLIException;

public class Ping extends PAPManagementCLI {
	
	private static final String USAGE = "[options]";
    private static final String[] commandNameValues = { "ping" };
    private static final String DESCRIPTION = "Ping a PAP (default endpoint is: " + 
    DEFAULT_SERVICE_URL + ").";

	public Ping() {
		super(commandNameValues, USAGE, DESCRIPTION, null);
	}

	@Override
	protected int executeCommand(CommandLine commandLine)
			throws CLIException, ParseException, RemoteException {
		
		String[] args = commandLine.getArgs();
		
		if (args.length > 1)
			throw new ParseException("Wrong number of arguments");
		
		String papVersion = papMgmtClient.ping();
		
		System.out.println("PAP successfully contacted: version=" + papVersion);
		
		return ExitStatus.SUCCESS.ordinal();
  	}

	@Override
	protected Options defineCommandOptions() {
		return null;
	}

}
