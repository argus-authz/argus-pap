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
import org.glite.authz.pap.ui.cli.papmanagement.ListPAPs;
import org.glite.authz.pap.ui.cli.papmanagement.Ping;
import org.glite.authz.pap.ui.cli.papmanagement.RefreshCache;
import org.glite.authz.pap.ui.cli.papmanagement.RemovePAP;
import org.glite.authz.pap.ui.cli.policymanagement.AddPolicies;
import org.glite.authz.pap.ui.cli.policymanagement.BanAttribute;
import org.glite.authz.pap.ui.cli.policymanagement.JobPriority;
import org.glite.authz.pap.ui.cli.policymanagement.ListPAPPolicies;
import org.glite.authz.pap.ui.cli.policymanagement.ListPolicies;
import org.glite.authz.pap.ui.cli.policymanagement.RemovePolicies;
import org.glite.authz.pap.ui.cli.policymanagement.UnBanAttribute;
import org.glite.authz.pap.ui.cli.policymanagement.UpdatePolicy;
import org.slf4j.LoggerFactory;


public class PAPCLI {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger( PAPCLI.class );

    private static final List<ServiceCLI> serviceCLIList = new LinkedList<ServiceCLI>();
    private static final List<ServiceCLI> policyMgmtCommandList = new LinkedList<ServiceCLI>();
    private static final List<ServiceCLI> papMgmtCommandList = new LinkedList<ServiceCLI>();
    private static final List<ServiceCLI> authzMgmtCommandList = new LinkedList<ServiceCLI>();

    protected static int hfWidth = 80;
    protected static final Options options = new Options();
    protected static final CommandLineParser parser = new GnuParser();

    protected static final HelpFormatter helpFormatter = new HelpFormatter();

    public static void main(String[] args) {
        new PAPCLI(args);
    }

    private static void defineCommands() {

        // Policy Management
    	policyMgmtCommandList.add(BanAttribute.dn());
    	policyMgmtCommandList.add(BanAttribute.fqan());
    	policyMgmtCommandList.add(UnBanAttribute.dn());
    	policyMgmtCommandList.add(UnBanAttribute.fqan());
    	policyMgmtCommandList.add(JobPriority.dn());
    	policyMgmtCommandList.add(JobPriority.fqan());
    	policyMgmtCommandList.add(new AddPolicies());
    	policyMgmtCommandList.add(new UpdatePolicy());
    	policyMgmtCommandList.add(new RemovePolicies());
    	policyMgmtCommandList.add(new ListPolicies());
    	policyMgmtCommandList.add(new ListPAPPolicies());

        // PAP Management
        papMgmtCommandList.add(new Ping());
        papMgmtCommandList.add(new AddPAP());
        papMgmtCommandList.add(new RemovePAP());
        papMgmtCommandList.add(new ListPAPs());
        papMgmtCommandList.add(new RefreshCache());
        
        // PAP Authz Management
        authzMgmtCommandList.add(new ListACL());
        authzMgmtCommandList.add(new AddACE());
        authzMgmtCommandList.add(new RemoveACE());
        
        serviceCLIList.addAll(policyMgmtCommandList);
        serviceCLIList.addAll(papMgmtCommandList);
        serviceCLIList.addAll(authzMgmtCommandList);

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

    private static String getCommandStringHelpMessage(String[] commandNameValues) {

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

    private static void printCommandHelpAndExit(ServiceCLI serviceCLI, int statusCode) {

        PrintWriter pw = new PrintWriter(System.out);

        serviceCLI.printHelpMessage(pw);

        pw.println();
        pw.flush();

        System.exit(statusCode);
    }

    private static void printGeneralHelpAndExit(int statusCode) {
        PrintWriter pw = new PrintWriter(System.out);

        helpFormatter.printUsage(pw, helpFormatter.getWidth(), "pap-admin <subcommand> [options]");
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "PAP command-line client.");
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(),
                "Type 'pap-admin <subcommand> -h' for help on a specific subcommand.");
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Global options:");
        helpFormatter.printOptions(pw, helpFormatter.getWidth(), options,
                helpFormatter.getLeftPadding(), helpFormatter.getDescPadding());
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "List of available subcommands grouped by " +
        		"category.");
        pw.println();

        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Policy management:");
        for (ServiceCLI serviceCLI : policyMgmtCommandList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI
                    .getCommandNameValues()));
        }
        pw.println();
        
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Distribution management:");
        for (ServiceCLI serviceCLI : papMgmtCommandList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI
                    .getCommandNameValues()));
        }
        pw.println();
        
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Authorization management:");
        for (ServiceCLI serviceCLI : authzMgmtCommandList) {
            helpFormatter.printWrapped(pw, hfWidth, getCommandStringHelpMessage(serviceCLI
                    .getCommandNameValues()));
        }

        pw.println();
        pw.flush();

        System.exit(statusCode);
    }

    public PAPCLI(String[] args) {
        
        helpFormatter.setLeftPadding(4);

        defineCommands();
        defineOptions();

        ServiceCLI serviceCLI = null;
        String command = null;

        try {

            CommandLine commandLine = parser.parse(options, args, true);

            if (commandLine.hasOption('h'))
                printGeneralHelpAndExit(0);
            
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            printGeneralHelpAndExit(1);
        }

        try {
            command = getCommand(args);
        } catch (ParseException e) {
            System.out.println("You must specify a command, available commands are listed below.");
            printGeneralHelpAndExit(1);
        }


        boolean commandFound = false;

        try {

            Iterator<ServiceCLI> serviceCLIIterator = serviceCLIList.iterator();
            while (serviceCLIIterator.hasNext()) {
                serviceCLI = serviceCLIIterator.next();
                if (serviceCLI.commandMatch(command)) {
                    
                    if (System.getProperty( "enablePapCliProfiling")!=null)
                        profileCommandExecution( serviceCLI, args );
                    else
                        serviceCLI.execute(args);
                    
                    commandFound = true;
                    break;
                }
            }

            if (!commandFound)
                System.out.println("Unknown command: " + command);

        } catch (ParseException e) {
            System.err.println("\nParsing failed.  Reason: " + e.getMessage() + "\n");
            printCommandHelpAndExit(serviceCLI, 4);
        } catch (HelpMessageException e) {
            printCommandHelpAndExit(serviceCLI, 4);
        } catch (RemoteException e) {
            System.out.println("Error invoking the '"+serviceCLI.getClass().getSimpleName()+"' method on remote endpoint: " + serviceCLI.getServiceClient().getTargetEndpoint());
            System.out.println("Reason: " + e.getMessage());
        }

    }

    protected void profileCommandExecution(ServiceCLI serviceCLI, String[] args) throws HelpMessageException, RemoteException, ParseException{
        
        int numSamples = 10; 
        
        long[] samples = new long[numSamples];
        
        for (int i=0; i < numSamples ; i++){
            long cmdFoundTime = System.currentTimeMillis();
            serviceCLI.execute(args);
            samples[i] = System.currentTimeMillis() - cmdFoundTime;
        }
        
        log.debug( "Avg '"+serviceCLI.getClass().getSimpleName()+"'cmd execution time: " + computeAvg( samples )+" msecs." );
        log.debug( "Fist '"+serviceCLI.getClass().getSimpleName()+"' execution (bootstrap) time: "+samples[0]+" msecs.");
        
    }
    protected long computeAvg(long[] values){
        
        long avg = 0;
        
        for (int i=1; i < values.length; i++)
            avg = (values[i]+avg)/2;
        
        return avg;
        
    }
    
    private static String fillWithSpaces(int n) {
        String s = "";
        for (int i=0; i<n; i++)
            s += " ";
        return s;
    }


}
