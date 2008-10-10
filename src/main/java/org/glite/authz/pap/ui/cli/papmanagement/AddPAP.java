package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.PAP;

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
    protected boolean executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if (args.length != 4)
            throw new ParseException("Wrong number of arguments");

        if (commandLine.hasOption(LOPT_PUBLIC))
            System.out.println(("PUBLIC"));

        String alias = args[1];
        String dn = args[2];
        String endpoint = args[3];

        PAP pap = new PAP(alias, endpoint, dn);

        papMgmtClient.addTrustedPAP(pap);

        return true;
    }

}
