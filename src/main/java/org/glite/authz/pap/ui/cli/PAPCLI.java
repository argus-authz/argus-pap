package org.glite.authz.pap.ui.cli;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.ui.cli.authzmanagement.AddACE;
import org.glite.authz.pap.ui.cli.authzmanagement.ListACL;
import org.glite.authz.pap.ui.cli.authzmanagement.RemoveACE;
import org.glite.authz.pap.ui.cli.papmanagement.AddPAP;
import org.glite.authz.pap.ui.cli.papmanagement.GetOrder;
import org.glite.authz.pap.ui.cli.papmanagement.ListPAPs;
import org.glite.authz.pap.ui.cli.papmanagement.Ping;
import org.glite.authz.pap.ui.cli.papmanagement.RefreshCache;
import org.glite.authz.pap.ui.cli.papmanagement.RemovePAP;
import org.glite.authz.pap.ui.cli.papmanagement.SetOrder;
import org.glite.authz.pap.ui.cli.policymanagement.AddPolicies;
import org.glite.authz.pap.ui.cli.policymanagement.BanAttribute;
import org.glite.authz.pap.ui.cli.policymanagement.ListPAPPolicies;
import org.glite.authz.pap.ui.cli.policymanagement.ListPolicies;
import org.glite.authz.pap.ui.cli.policymanagement.RemoveAllPolicies;
import org.glite.authz.pap.ui.cli.policymanagement.RemovePolicies;
import org.glite.authz.pap.ui.cli.policymanagement.UnBanAttribute;
import org.glite.authz.pap.ui.cli.policymanagement.UpdatePolicy;
import org.glite.authz.pap.ui.cli.samlclient.SAMLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PAPCLI {

    private static final Logger log = LoggerFactory.getLogger(PAPCLI.class);

    public static void main(String[] args) {

        PAPCLI cli = new PAPCLI(args);

        try {
            cli.parseCommandLine();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        int exitStatus = cli.executeCommand();

        for (ServiceCLI.ExitStatus es : ServiceCLI.ExitStatus.values()) {
            if (es.ordinal() == exitStatus) {
                log.info(String.format("Exit status (%s): %s %d", cli.getCommandName(), es.toString(), exitStatus));
            }
        }

        System.exit(exitStatus);

    }

    private final List<ServiceCLI> serviceCLIList = new LinkedList<ServiceCLI>();
    private final List<ServiceCLI> policyMgmtCommandList = new LinkedList<ServiceCLI>();
    private final List<ServiceCLI> papMgmtCommandList = new LinkedList<ServiceCLI>();
    private final List<ServiceCLI> authzMgmtCommandList = new LinkedList<ServiceCLI>();
    private final List<ServiceCLI> testCommandList = new LinkedList<ServiceCLI>();

    protected int hfWidth = 80;
    protected final Options options = new Options();
    protected final CommandLineParser parser = new GnuParser();
    protected final HelpFormatter helpFormatter = new HelpFormatter();
    protected ServiceCLI serviceCLI = null;
    protected String[] args;
    protected boolean printGeneralHelpMessage = false;

    public PAPCLI(String[] args) {

        this.args = args;

        helpFormatter.setLeftPadding(4);

        defineCommands();
        defineOptions();

    }

    public int executeCommand() {

        if (printGeneralHelpMessage) {
            printGeneralHelp();
            return ServiceCLI.ExitStatus.SUCCESS.ordinal();
        }

        if (serviceCLI == null)
            return ServiceCLI.ExitStatus.INITIALIZATION_ERROR.ordinal();

        int exitStatus;

        try {

            if (System.getProperty("enablePapCliProfiling") != null)
                exitStatus = profileCommandExecution(serviceCLI, args);
            else
                exitStatus = serviceCLI.execute(args);

        } catch (ParseException e) {
            System.err.println("\nParsing failed.  Reason: " + e.getMessage() + "\n");
            return ServiceCLI.ExitStatus.PARSE_ERROR.ordinal();

        } catch (HelpMessageException e) {
            printCommandHelp(serviceCLI);
            return ServiceCLI.ExitStatus.SUCCESS.ordinal();

        } catch (RemoteException e) {
            log.error("Remote exception", e);
            System.out.println("Error invoking the '" + serviceCLI.getClass().getSimpleName() + "' method on remote endpoint: "
                    + serviceCLI.getServiceClient().getTargetEndpoint());
            System.out.println("Reason: " + e.getMessage());
            return ServiceCLI.ExitStatus.REMOTE_EXCEPTION.ordinal();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            log.error("admin exception", e);
            return ServiceCLI.ExitStatus.FAILURE.ordinal();
        }

        return exitStatus;
    }

    public void parseCommandLine() throws ParseException {

        CommandLine commandLine = parser.parse(options, args, true);

        if (commandLine.hasOption('h'))
            printGeneralHelpMessage = true;
        else
            printGeneralHelpMessage = false;

        String command = getCommand(args);

        boolean commandFound = false;

        Iterator<ServiceCLI> serviceCLIIterator = serviceCLIList.iterator();
        while (serviceCLIIterator.hasNext()) {
            serviceCLI = serviceCLIIterator.next();
            if (serviceCLI.commandMatch(command)) {
                commandFound = true;
                break;
            }
        }

        if (!commandFound) {
            serviceCLI = null;
            throw new ParseException("Unknown command: " + command);
        }

    }

    public String getCommandName() {

        if (serviceCLI != null)
            return serviceCLI.getCommandNameValues()[0];

        return null;
    }

    private void defineCommands() {

        // Policy Management
        policyMgmtCommandList.add(BanAttribute.dn());
        policyMgmtCommandList.add(BanAttribute.fqan());
        policyMgmtCommandList.add(UnBanAttribute.dn());
        policyMgmtCommandList.add(UnBanAttribute.fqan());
        policyMgmtCommandList.add(new AddPolicies());
        policyMgmtCommandList.add(new UpdatePolicy());
        policyMgmtCommandList.add(new RemovePolicies());
        policyMgmtCommandList.add(new RemoveAllPolicies());
        policyMgmtCommandList.add(new ListPolicies());
        policyMgmtCommandList.add(new ListPAPPolicies());

        // PAP Management
        papMgmtCommandList.add(new Ping());
        papMgmtCommandList.add(new AddPAP());
        papMgmtCommandList.add(new RemovePAP());
        papMgmtCommandList.add(new ListPAPs());
        papMgmtCommandList.add(new RefreshCache());
        papMgmtCommandList.add(new GetOrder());
        papMgmtCommandList.add(new SetOrder());

        // PAP Authz Management
        authzMgmtCommandList.add(new ListACL());
        authzMgmtCommandList.add(new AddACE());
        authzMgmtCommandList.add(new RemoveACE());
        
        // Test
        testCommandList.add(new SAMLClient());

        serviceCLIList.addAll(policyMgmtCommandList);
        serviceCLIList.addAll(papMgmtCommandList);
        serviceCLIList.addAll(authzMgmtCommandList);
        serviceCLIList.addAll(testCommandList);

    }

    private void defineOptions() {
        options.addOption("h", "help", false, "Print this message");
    }

    private String getCommandStringHelpMessage(String[] commandNameValues) {

        String commandString = fillWithSpaces(helpFormatter.getLeftPadding()) + commandNameValues[0];

        if (commandNameValues.length > 1) {

            commandString += " (";

            for (int i = 1; i < commandNameValues.length; i++) {
                commandString += commandNameValues[i];
                if (i < commandNameValues.length - 1)
                    commandString += ", ";
            }

            commandString += ")";
        }

        return commandString;
    }

    private void printCommandHelp(ServiceCLI serviceCLI) {

        PrintWriter pw = new PrintWriter(System.out);

        serviceCLI.printHelpMessage(pw);

        pw.println();
        pw.flush();

    }

    private void printGeneralHelp() {
        PrintWriter pw = new PrintWriter(System.out);

        helpFormatter.printUsage(pw, helpFormatter.getWidth(), "pap-admin <subcommand> [options]");
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "PAP command-line client.");
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(),
                "Type 'pap-admin <subcommand> -h' for help on a specific subcommand.");
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Global options:");
        helpFormatter.printOptions(pw, helpFormatter.getWidth(), options, helpFormatter.getLeftPadding(), helpFormatter
                .getDescPadding());
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "List of available subcommands grouped by " + "category.");
        pw.println();

        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Policy management:");
        for (ServiceCLI serviceCLI : policyMgmtCommandList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI.getCommandNameValues()));
        }
        pw.println();

        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Distribution management:");
        for (ServiceCLI serviceCLI : papMgmtCommandList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI.getCommandNameValues()));
        }
        pw.println();

        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Authorization management:");
        for (ServiceCLI serviceCLI : authzMgmtCommandList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI.getCommandNameValues()));
        }
        pw.println();
        
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Test utils:");
        for (ServiceCLI serviceCLI : testCommandList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI.getCommandNameValues()));
        }

        pw.println();
        pw.flush();

    }

    protected long computeAvg(long[] values) {

        long avg = 0;

        for (int i = 1; i < values.length; i++)
            avg = (values[i] + avg) / 2;

        return avg;

    }

    protected String fillWithSpaces(int n) {
        String s = "";
        for (int i = 0; i < n; i++)
            s += " ";
        return s;
    }

    protected String getCommand(String[] args) throws ParseException {
        for (String arg : args) {
            if (!arg.startsWith("-"))
                return arg;
        }
        throw new ParseException("Missing command");
    }

    protected int profileCommandExecution(ServiceCLI serviceCLI, String[] args) throws HelpMessageException, RemoteException,
            ParseException {

        int status = ServiceCLI.ExitStatus.FAILURE.ordinal();
        int numSamples = 10;

        long[] samples = new long[numSamples];

        for (int i = 0; i < numSamples; i++) {
            long cmdFoundTime = System.currentTimeMillis();
            status = serviceCLI.execute(args);
            samples[i] = System.currentTimeMillis() - cmdFoundTime;
        }

        log.debug("Avg '" + serviceCLI.getClass().getSimpleName() + "'cmd execution time: " + computeAvg(samples) + " msecs.");
        log.debug("Fist '" + serviceCLI.getClass().getSimpleName() + "' execution (bootstrap) time: " + samples[0] + " msecs.");

        return status;

    }

}
