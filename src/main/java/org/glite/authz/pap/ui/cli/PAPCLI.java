package org.glite.authz.pap.ui.cli;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.ServiceClientFactory;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.ui.cli.papmanagement.AddPAP;
import org.glite.authz.pap.ui.cli.papmanagement.RemovePAP;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;

public class PAPCLI {
    
    private static final String OPT_URL = "url"; 
    private static final String OPT_CERT = "cert";
    private static final String OPT_KEY = "key";
    private static final String OPT_PASSWORD = "password";
    private static final String DEFAULT_SERVICE_URL = "https://localhost:8443/pap/services/";

    protected static final Options options = new Options();
    protected static final CommandLineParser parser = new GnuParser(); 
    protected static final HelpFormatter helpFormatter = new HelpFormatter();
    
    private static final List<ServiceCLI> serviceCommandList = new LinkedList<ServiceCLI>();
    
    public static void main(String[] args) throws ConfigurationException, RemoteException {
        
        try {
            init();
        } catch(PAPConfigurationException e) {
            System.out.println("Ignoring configuration exception...");
        }
        new PAPCLI(args);
    }
    
    private static void defineCommands() {
        serviceCommandList.add(new AddPAP());
        serviceCommandList.add(new RemovePAP());
    }
    
    @SuppressWarnings("static-access")
    private static void defineCommandLineOptions() {

        options.addOption("h", "help", false, "print this message");

        options.addOption(OptionBuilder.hasArg().withLongOpt("url").withDescription(
                "Specifies the target PAP endpoint to be contacted.").create(OPT_URL));

        options.addOption(OptionBuilder.hasArg().withLongOpt("cert").withDescription(
                "Specifies non-standard user certificate.").create(OPT_CERT));

        options.addOption(OptionBuilder.hasArg().withLongOpt("key").withDescription(
                "Specifies non-standard user private key.").create(OPT_KEY));

        options.addOption(OptionBuilder.hasArg().withLongOpt("password").withDescription(
                "Specifies a password that is used to decrypt the user's private key.").create(
                OPT_PASSWORD));
        
        for (Option opt:PolicyManagementServiceCLI.getOptions()) {
            options.addOption(opt);
        }
        
        for (Option opt:PAPManagementServiceCLI.getOptions()) {
            options.addOption(opt);
        }
        
    }

    private static void init() throws ConfigurationException {
        DefaultBootstrap.bootstrap();
        XMLConfigurator xmlConfigurator = new XMLConfigurator();

        // Needed because of a "bug" in opensaml 2.1.0... can be removed
        // when opensaml is updated
        xmlConfigurator.load(Configuration.class
            .getResourceAsStream("/opensaml_bugfix.xml"));
    }

    private static void printHelpAndExit(int statusCode) {
        helpFormatter.printHelp("CLI <option>", options);
        System.exit(statusCode);
    }

    public PAPCLI(String[] args) throws RemoteException {
        defineCommands();
        for (ServiceCLI s:serviceCommandList) {
            System.out.println(s.getCommandName());
        }
        System.exit(0);
        defineCommandLineOptions();
        
        try {
            
            CommandLine commandLine = parser.parse( options, args );
            
            ServiceClientFactory serviceClientFactory = ServiceClientFactory.getServiceClientFactory();
            ServiceClient serviceClient = serviceClientFactory.createServiceClient();
            
            if (commandLine.hasOption(OPT_URL))
                serviceClient.setTargetEndpoint(commandLine.getOptionValue(OPT_URL));
            else
                serviceClient.setTargetEndpoint(DEFAULT_SERVICE_URL);
            if (commandLine.hasOption(OPT_CERT))
                serviceClient.setClientCertificate(commandLine.getOptionValue(OPT_CERT));
            if (commandLine.hasOption(OPT_KEY))
                serviceClient.setClientPrivateKey(OPT_KEY);
            if (commandLine.hasOption(OPT_PASSWORD))
                serviceClient.setClientPrivateKeyPassword(OPT_PASSWORD);
            
            if (commandLine.hasOption('h'))
                printHelpAndExit(0);
            else if (PolicyManagementServiceCLI.execute(commandLine, serviceClient))
                return;
            else if (PAPManagementServiceCLI.execute(commandLine, serviceClient))
                return;
            else {
                printHelpAndExit(1);
            }
            
        }
        catch( ParseException e ) {
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
            printHelpAndExit(1);
        }

    }
    
}
