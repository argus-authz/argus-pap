package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;

public class AddPAP extends PAPManagementCLI {

    private static final String LOPT_PUBLIC = "public";
    private static final String LOPT_PRIVATE = "private";
    private static final String USAGE = "<alias> <endpoint> <dn> [options]";
    private static final String[] commandNameValues = { "add-pap", "apap" };
    private static final String DESCRIPTION = "Add a trusted PAP to get policies from.";
    
    public AddPAP() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false).withDescription(
                "Set the remote PAP as public (allow to distribute its policies)").withLongOpt(
                LOPT_PUBLIC).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(
                "Set the remote PAP as private (default)").withLongOpt(LOPT_PRIVATE).create());
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if (args.length != 4)
            throw new ParseException("Wrong number of arguments");

        boolean isPublic = false;
        
        if (commandLine.hasOption(LOPT_PUBLIC))
            isPublic = true;

        String alias = args[1];
        String endpoint = args[2];
        String dn = args[3];

        PAP pap = new PAP(alias, endpoint, dn, isPublic);
        
        String msg = "Adding trusted PAP: ";
        
        if (verboseMode)
            System.out.println(msg + pap.toFormattedString(0, msg.length() + 4));
        
        if (papMgmtClient.exists(pap.getPapId())) {
        	System.out.println("PAP already exists.");
        	return ExitStatus.FAILURE.ordinal();
        }
        
        PAPData papData = new PAPData();
        
        papData.setAlias(pap.getAlias());
        papData.setDn(pap.getDn());
        papData.setHostname(pap.getHostname());
        papData.setPapId(pap.getPapId());
        papData.setPath(pap.getPath());
        papData.setPort(pap.getPort());
        papData.setProtocol(pap.getProtocol());
        papData.setVisibilityPublic(pap.isPublic());
        
        papMgmtClient.addTrustedPAP(papData);
        
        if (verboseMode)
            System.out.println("Success: new trusted PAP has been added.");
        
        return ExitStatus.SUCCESS.ordinal();

    }

}
