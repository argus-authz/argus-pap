package org.glite.authz.pap.ui;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;

public class PAPCLI {
    private static final char OPT_POLICYMGMT_ADD_POLICY = 'a';
    private static final char OPT_POLICYMGMT_ADD_POLICY_FILE = 'A';
    private static final char OPT_POLICYMGMT_REMOVE_POLICY = 'r';
    private static final char OPT_POLICYMGMT_UPDATE_POLICY = 'u';
    private static final char OPT_POLICYMGMT_LIST = 'l';
    private static final String OPT_POLICYMGMT_XACML= "xacml";
    
    private static final char OPT_PAPMGMT_PING = 'p';
    private static final char OPT_PAPMGMT_ADD_PAP = 'P';
    
    protected static final Options options = new Options();
    protected static final CommandLineParser parser = new GnuParser(); 
    protected static final HelpFormatter helpFormatter = new HelpFormatter();
    
    public static void main(String[] args) throws ConfigurationException, RemoteException {
        
        try {
            init();
        } catch(PAPConfigurationException e) {
            System.out.println("Ignoring configuration exception...");
        }
        new PAPCLI(args);
    }
    
    @SuppressWarnings("static-access")
    private static void defineCommandLineOptions() {
        // General
        options.addOption("h", "help", false, "print this message");
        
        // PolicyManagement
        options.addOption(OptionBuilder.hasOptionalArgs().withLongOpt("add-policy").withDescription("Add policy").create(OPT_POLICYMGMT_ADD_POLICY));
        options.addOption(OptionBuilder.hasArgs().withLongOpt("add-policy-file").withDescription("Add policies from file").create(OPT_POLICYMGMT_ADD_POLICY_FILE));
        options.addOption(OptionBuilder.hasArgs().withLongOpt("remove").withDescription("Remove policy").create(OPT_POLICYMGMT_REMOVE_POLICY));
        options.addOption(OptionBuilder.hasArgs().withLongOpt("update").withDescription("Update policy").create(OPT_POLICYMGMT_UPDATE_POLICY));
        options.addOption(OptionBuilder.hasOptionalArgs().withLongOpt("list").withDescription("List policies").create(OPT_POLICYMGMT_LIST));
        options.addOption(OptionBuilder.withLongOpt(OPT_POLICYMGMT_XACML).withDescription("Show XACML when listing policies").create());
        
        // PAPManagement
        options.addOption(OptionBuilder.hasOptionalArgs().withLongOpt("ping").withDescription("Ping a PAP").create(OPT_PAPMGMT_PING));
        options.addOption(OptionBuilder.hasArgs().withLongOpt("add-pap").withDescription("Update policy").create(OPT_PAPMGMT_ADD_PAP));
    }

    private static void init() throws ConfigurationException {
        DefaultBootstrap.bootstrap();
        XMLConfigurator xmlConfigurator = new XMLConfigurator();

        // Needed because of a "bug" in opensaml 2.1.0... can be removed
        // when opensaml is updated
        xmlConfigurator.load(Configuration.class
            .getResourceAsStream("/opensaml_bugfix.xml"));
        
        //RepositoryManager.getInstance().bootstrap();
        
    }

    private static void printHelpAndExit(int statusCode) {
        helpFormatter.printHelp("CLI <option>", options);
        System.exit(statusCode);
    }

    public PAPCLI(String[] args) throws RemoteException {
        
        defineCommandLineOptions();
        
        try {
            
            CommandLine commandLine = parser.parse( options, args );
            
            if (commandLine.hasOption('h'))
                printHelpAndExit(0);
            // PolicyManagementService
            else if (commandLine.hasOption(OPT_POLICYMGMT_LIST))
                PolicyManagementServiceCLI.list(commandLine.hasOption(OPT_POLICYMGMT_XACML));
            else if (commandLine.hasOption(OPT_POLICYMGMT_ADD_POLICY))
                PolicyManagementServiceCLI.addPolicy(commandLine.getOptionValues(OPT_POLICYMGMT_ADD_POLICY));
            else if (commandLine.hasOption(OPT_POLICYMGMT_REMOVE_POLICY))
                PolicyManagementServiceCLI.removePolicy(commandLine.getOptionValues(OPT_POLICYMGMT_REMOVE_POLICY));
            // PAPManagementService
            else if (commandLine.hasOption(OPT_PAPMGMT_PING))
                PAPManagementServiceCLI.ping();
            else if (commandLine.hasOption(OPT_PAPMGMT_ADD_PAP)) {
                PAPManagementServiceCLI.addTrustedPAP(commandLine.getOptionValues(OPT_PAPMGMT_ADD_PAP));
            }
            else {
                System.out.println("NIENTE");
                printHelpAndExit(1);
            }
            
        }
        catch( ParseException e ) {
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
            printHelpAndExit(1);
        }

    }
    
    private static void prova(String[] args) {
        Options opt = new Options();
        OptionGroup groupOpt = new OptionGroup();
        
        groupOpt.addOption(OptionBuilder.hasArgs().withDescription("Update policy").create('a'));
        groupOpt.addOption(OptionBuilder.withDescription("Update policy").create('b'));
        opt.addOption(OptionBuilder.hasArgs().withDescription("Update policy").create('c'));
        opt.addOption(OptionBuilder.withLongOpt("xacml").withDescription("Update policy").create());
        opt.addOptionGroup(groupOpt);
        try {
            CommandLine commandLine = parser.parse( opt, args );
            
            if (commandLine.hasOption('a'))
                System.out.println("Option 'a'");
            if (commandLine.hasOption('b'))
                System.out.println("Option 'b'");
            if (commandLine.hasOption('c'))
                System.out.println("Option 'c'");
            if (commandLine.hasOption("xacml"))
                System.out.println("Option 'XACML'");
        } catch (ParseException e) {
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
        }
        System.exit(0);
    }
}
