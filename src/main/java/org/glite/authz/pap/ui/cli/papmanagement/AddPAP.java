package org.glite.authz.pap.ui.cli.papmanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPData;

public class AddPAP extends PAPManagementCLI {

    private static final String[] commandNameValues = { "add-pap", "apap" };
    private static final String DESCRIPTION = "Add a trusted PAP to get policies from.\n"
            + "<alias> is a friendly name (it has to be unique) used to identify the PAP\n" + "<dn> DN of the PAP\n"
            + "<host> hostname of the PAP machine\n" + "<port> the port the PAP is listening to\n"
            + "[path] root path of the services of the PAP (the default is " + PAP.DEFAULT_SERVICES_ROOT_PATH + ")\n";
    private static final String LONG_DESCRIPTION = "\nExample:\n" + "\t pap-admin " + commandNameValues[0]
            + "cnaf_pap \"/C=IT/O=INFN/OU=Host/L=CNAF/CN=test.cnaf.infn.it\" " + "test.cnaf.infn.it " + PAP.DEFAULT_PORT;
    private static final String LOPT_PRIVATE = "private";
    private static final String LOPT_PUBLIC = "public";
    private static final String USAGE = "<alias> [<dn> <host> <port> [path]] [options]";

    public AddPAP() {
        super(commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
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

        String msg = "Adding trusted PAP: ";

        if (verboseMode) {
            System.out.println(msg + pap.toFormattedString(0, msg.length()));
        }

        if (papMgmtClient.exists(pap.getAlias())) {
            System.out.println("PAP already exists.");
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

        papMgmtClient.addPAP(papData);

        if (verboseMode) {
            System.out.println("Success: new trusted PAP has been added.");
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
