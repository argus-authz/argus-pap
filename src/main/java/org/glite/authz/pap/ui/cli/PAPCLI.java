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
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.ui.cli.papmanagement.AddPAP;
import org.glite.authz.pap.ui.cli.papmanagement.ListPAPs;
import org.glite.authz.pap.ui.cli.papmanagement.RemovePAP;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;

public class PAPCLI {

    private static final List<ServiceCLI> serviceCLIList = new LinkedList<ServiceCLI>();

    protected static int hfWidth = 80;
    protected static final Options options = new Options();
    protected static final CommandLineParser parser = new GnuParser();

    protected static final HelpFormatter helpFormatter = new HelpFormatter();

    public static void main(String[] args) throws ConfigurationException {
        try {
            init();
        } catch (PAPConfigurationException e) {
            System.out.println("Ignoring configuration exception...");
        }
        new PAPCLI(args);
    }

    private static void defineCommands() {

        serviceCLIList.add(new AddPAP());
        serviceCLIList.add(new RemovePAP());
        serviceCLIList.add(new ListPAPs());

    }

    private static void defineOptions() {
        options.addOption("h", "help", false, "Print this message");
    }

    private static String getCommand(String[] args) throws ParseException {
        for (String arg : args) {
            if (!arg.startsWith("-"))
                return arg;
        }
        throw new ParseException("Missing command");
    }

    private static String getCommandStringHelpMessage(String[] commandNameValues, int maxCommandLen) {
        
        int currCommandLen = commandNameValues[0].length();
        String commandString = "    " + commandNameValues[0];
        String padding = getPadding(maxCommandLen - currCommandLen);
        
        if (commandNameValues.length > 1) {
            
            commandString +=  " (";
            
            for (int i = 1; i < commandNameValues.length; i++) {
                commandString += commandNameValues[i];
                if (i < commandNameValues.length - 1)
                    commandString += ", ";
            }
            
            commandString += ")";
        }
        
        return commandString;
    }

    private static int getMaximumCommandLength(List<ServiceCLI> serviceCLIList) {
        int maxLen = 0;
        for (ServiceCLI serviceCLI : serviceCLIList) {
            String command = serviceCLI.getCommandNameValues()[0];
            if (command.length() > maxLen)
                maxLen = command.length();
        }
        return maxLen;
    }

    private static String getPadding(int paddingLen) {
        String padding = new String(" ");
        for (int i = 0; i < paddingLen; i++)
            padding += " ";
        return padding;
    }

    private static void init() throws ConfigurationException {
        DefaultBootstrap.bootstrap();
        XMLConfigurator xmlConfigurator = new XMLConfigurator();

        // Needed because of a "bug" in opensaml 2.1.0... can be removed
        // when opensaml is updated
        xmlConfigurator.load(Configuration.class.getResourceAsStream("/opensaml_bugfix.xml"));
    }

    private static void printCommandHelpAndExit(ServiceCLI serviceCLI, int statusCode) {

        PrintWriter pw = new PrintWriter(System.out);

        serviceCLI.printHelpMessage(pw);

        pw.println();
        pw.flush();

        System.exit(statusCode);
    }

    private static void printGeneralHelpAndExit(int statusCode) {
        PrintWriter pw = new PrintWriter(System.out);

        helpFormatter.printUsage(pw, helpFormatter.getWidth(), "pap <subcommand> [options]");
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "PAP command-line client.");
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(),
                "Type 'pap <subcommand> -h' for help on a specific subcommand.");
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Global options:");
        helpFormatter.printOptions(pw, helpFormatter.getWidth(), options,
                helpFormatter.getLeftPadding(), helpFormatter.getDescPadding());
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Available subcommands:");

        int maxCommandLen = getMaximumCommandLength(serviceCLIList);

        for (ServiceCLI serviceCLI : serviceCLIList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI
                    .getCommandNameValues(), maxCommandLen));
        }

        pw.println();
        pw.flush();

        System.exit(statusCode);
    }

    public PAPCLI(String[] args) {

        defineCommands();
        defineOptions();

        ServiceCLI serviceCLI = null;
        String command = null;

        try {

            CommandLine commandLine = parser.parse(options, args, true);

            if (commandLine.hasOption('h'))
                printGeneralHelpAndExit(0);

            command = getCommand(args);

        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            printGeneralHelpAndExit(1);
        }

        boolean commandFound = false;

        try {

            Iterator<ServiceCLI> serviceCLIIterator = serviceCLIList.iterator();
            while (serviceCLIIterator.hasNext()) {
                serviceCLI = serviceCLIIterator.next();
                if (serviceCLI.commandMatch(command)) {
                    commandFound = serviceCLI.execute(args);
                    break;
                }
            }

            if (!commandFound)
                System.out.println("Unknown command: " + command);

        } catch (ParseException e) {
            System.err.println("\nParsing failed.  Reason: " + e.getMessage() + "\n");
            printCommandHelpAndExit(serviceCLI, 1);
        } catch (HelpMessageException e) {
            printCommandHelpAndExit(serviceCLI, 1);
        } catch (RemoteException e) {
            System.err
                    .println("Cannot conncet to: " + serviceCLI.getServiceClient().getTargetEndpoint());
        }

    }

}
