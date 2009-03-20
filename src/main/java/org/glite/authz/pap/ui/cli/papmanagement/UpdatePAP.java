package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;

public class UpdatePAP extends PAPManagementCLI {

    private static final String[] commandNameValues = { "update-pap", "upap" };
    private static final String DESCRIPTION = "Update information for a PAP. The input is the same as for the \"add-pap\" command, " +
    		"the effect is to update old iformation with the new one.\n";
    private static final String LOPT_PRIVATE = "private";
    private static final String LOPT_PUBLIC = "public";
    private static final String USAGE = "<alias> [<dn> <host> <port> [path]] [options]";

    public UpdatePAP() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription("Set the remote PAP as public (allow to distribute its policies)")
                                       .withLongOpt(LOPT_PUBLIC)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription("Set the remote PAP as private (default)")
                                       .withLongOpt(LOPT_PRIVATE)
                                       .create());
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_LOCAL_DESCRIPTION)
                                       .withLongOpt(OPT_LOCAL_LONG)
                                       .create(OPT_LOCAL));
        options.addOption(OptionBuilder.hasArg(false)
                                       .withDescription(OPT_REMOTE_DESCRIPTION)
                                       .withLongOpt(OPT_REMOTEL_LONG)
                                       .create(OPT_REMOTE));
        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        String[] args = commandLine.getArgs();

        if ((args.length != 5) && (args.length != 6) && (args.length != 2)) {
            throw new ParseException("Wrong number of arguments");
        }

        PAP.PSType pstype;
        boolean isPublic = false;

        if (args.length != 2) {
            pstype = PAP.PSType.REMOTE;
        } else {
            pstype = PAP.PSType.LOCAL;
        }

        if (commandLine.hasOption(OPT_LOCAL)) {
            pstype = PAP.PSType.LOCAL;
        }

        if (commandLine.hasOption(OPT_REMOTE)) {
            pstype = PAP.PSType.REMOTE;
        }

        if (commandLine.hasOption(LOPT_PUBLIC)) {
            isPublic = true;
        }

        String alias = args[1];
        String dn = null;
        String host = null;
        String port = null;
        String path = null;

        if (args.length != 2) {
            dn = args[2];
            host = args[3];
            port = args[4];
            if (args.length == 6) {
                path = args[5];
            }
        }

        PAP pap = new PAP(alias, pstype, dn, host, port, path, isPublic);

        String msg = "Updating PAP: ";

        if (verboseMode) {
            System.out.println(msg + pap.toFormattedString(0, msg.length()));
        }

        if (!(papMgmtClient.exists(pap.getAlias()))) {
            System.out.println("PAP doesn't exists.");
            return ExitStatus.FAILURE.ordinal();
        }

        PAPData papData = new PAPData();

        papData.setAlias(pap.getAlias());
        papData.setType(pstype.toString());
        papData.setDn(pap.getDn());
        papData.setHostname(pap.getHostname());
        papData.setId(pap.getPapId());
        papData.setPath(pap.getPath());
        papData.setPort(pap.getPort());
        papData.setProtocol(pap.getProtocol());
        papData.setVisibilityPublic(pap.isVisibilityPublic());

        papMgmtClient.updatePAP(papData);

        if (verboseMode) {
            System.out.println("Success: new trusted PAP has been added.");
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
