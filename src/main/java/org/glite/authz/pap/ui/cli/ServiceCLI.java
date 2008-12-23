package org.glite.authz.pap.ui.cli;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.ServiceClientFactory;

public abstract class ServiceCLI {
    
    private static final HelpFormatter helpFormatter = new HelpFormatter();
    private static final String LOPT_CERT = "cert";
    private static final String LOPT_HOST = "host";
    private static final String LOPT_KEY = "key";
    private static final String LOPT_PASSWORD = "password";
    private static final String LOPT_URL = "url";
    private static final String LOPT_VERBOSE = "verbose";
    private static final String OPT_CERT = "cert";
    private static final String OPT_CERT_DESCRIPTION = "Specifies non-standard user certificate.";
    private static final String OPT_HOST = "host";
    private static final String OPT_HOST_DESCRIPTION = "Specifies the target PAP hostname (default is localhost). " +
    		"This option defines the PAP endpoint to be contacted as follows: https://hostname:8443/pap/services";
    private static final String OPT_KEY = "key";
    private static final String OPT_KEY_DESCRIPTION = "Specifies non-standard user private key.";
    private static final String OPT_PASSWORD = "password";
    private static final String OPT_PASSWORD_DESCRIPTION = "Specifies the password used to decrypt the user's private key.";
    private static final String OPT_VERBOSE_DESCRIPTION = "Verbose mode.";
    private static final String OPT_URL = "url";
    private static final String OPT_VERBOSE = "v";
    
    protected static final String DEFAULT_HOST = "localhost";
    protected static final String DEFAULT_SERVICE_URL = "https://%s:8443/pap/services/";
    protected static final String LOPT_HELP = "help";
    protected static final String LOPT_PRIVATE = "private";
    protected static final String LOPT_PUBLIC = "public";
    protected static final String OPT_HELP = "h";
    protected static final String OPT_HELP_DESCRIPTION = "Print this message.";
    
    protected static final CommandLineParser parser = new GnuParser();
    private String[] commandNameValues;
    private Options commandOptions;
    private String descriptionText;
    private Options globalOptions;
    private String longDescriptionText;
    private Options options;
    private final ServiceClient serviceClient;
    private String usageText;
    protected boolean verboseMode = false;
    
    @SuppressWarnings( "unchecked" )
    public ServiceCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        
        ServiceClientFactory serviceClientFactory = ServiceClientFactory.getServiceClientFactory();
        serviceClient = serviceClientFactory.createServiceClient();
        
        helpFormatter.setWidth(80);
        helpFormatter.setLeftPadding(4);
        
        this.commandNameValues = commandNameValues;
        this.usageText = usage;
        this.descriptionText = description;
        this.longDescriptionText = longDescription;
        
        commandOptions = defineCommandOptions();
        if (commandOptions == null)
            commandOptions = new Options();
        
        commandOptions.addOption(OPT_HELP, LOPT_HELP, false, OPT_HELP_DESCRIPTION);

        globalOptions = defineGlobalOptions();
        
        options = new Options();
        Collection<Option> optionsList = commandOptions.getOptions();
        for (Option opt : optionsList) {
            options.addOption(opt);
        }
        
        optionsList = globalOptions.getOptions();
        for (Option opt : optionsList) {
            options.addOption(opt);
        }
    }
    
    public boolean commandMatch(String command) {
        for (String value : commandNameValues) {
            if (value.equals(command))
                return true;
        }
        return false;
    }
    
    public void execute(String[] args) throws ParseException, HelpMessageException, RemoteException {
        
        CommandLine commandLine = parser.parse(options, args);
        
        if (commandLine.hasOption(OPT_HELP))
            throw new HelpMessageException();
        
        if (commandLine.hasOption(OPT_URL))
            serviceClient.setTargetEndpoint(commandLine.getOptionValue(OPT_URL));
        else if (commandLine.hasOption(OPT_HOST))
            serviceClient.setTargetEndpoint(String.format(DEFAULT_SERVICE_URL, commandLine.getOptionValue(OPT_HOST)));
        else
            serviceClient.setTargetEndpoint(String.format(DEFAULT_SERVICE_URL, DEFAULT_HOST));
        
        if (commandLine.hasOption(OPT_CERT)) 
            serviceClient.setClientCertificate(commandLine.getOptionValue(OPT_CERT));
        
        
        if (commandLine.hasOption(OPT_KEY))
            serviceClient.setClientPrivateKey(commandLine.getOptionValue(OPT_KEY));
        
        if (commandLine.hasOption(OPT_PASSWORD))
            serviceClient.setClientPrivateKeyPassword(commandLine.getOptionValue(OPT_PASSWORD));
        
        if (commandLine.hasOption(OPT_VERBOSE))
            verboseMode = true;
        
        executeCommandService(commandLine, serviceClient);
        
    }
    
    public String[] getCommandNameValues() {
        return commandNameValues;
    }
    
    public ServiceClient getServiceClient() {
        return serviceClient;
    }
    
    public void printHelpMessage(PrintWriter pw) {
        String syntax = commandNameValues[0] + " " + usageText;
        
        helpFormatter.printUsage(pw, helpFormatter.getWidth(), syntax);
        
        if (descriptionText != null) {
            pw.println();
            helpFormatter.printWrapped(pw, helpFormatter.getWidth(), descriptionText);
        }
        
        if (longDescriptionText != null) {
            pw.println();
            helpFormatter.printWrapped(pw, helpFormatter.getWidth(), longDescriptionText);
        }
        
        // command specific options
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Valid options:");
        helpFormatter.printOptions(pw, helpFormatter.getWidth(), commandOptions, helpFormatter
                .getLeftPadding(), helpFormatter.getDescPadding());
        
        // global options
        pw.println();
        helpFormatter.printWrapped(pw, helpFormatter.getWidth(), "Global options:");
        helpFormatter.printOptions(pw, helpFormatter.getWidth(), globalOptions, helpFormatter
                .getLeftPadding(), helpFormatter.getDescPadding());
    }
    
    @SuppressWarnings("static-access")
    private Options defineGlobalOptions() {
        
        Options options = new Options();
        OptionGroup mutuallyExclusiveOptions = new OptionGroup();
        
        mutuallyExclusiveOptions.addOption(OptionBuilder.hasArg().withLongOpt(LOPT_URL)
                .withDescription("Specifies the target PAP endpoint (default: "
                        + String.format(DEFAULT_SERVICE_URL, DEFAULT_HOST) + ").").create(OPT_URL));
        mutuallyExclusiveOptions.addOption(OptionBuilder.hasArg().withLongOpt(LOPT_HOST)
                .withDescription(OPT_HOST_DESCRIPTION).create(OPT_HOST));
        
        options.addOptionGroup(mutuallyExclusiveOptions);
        
        options.addOption(OptionBuilder.hasArg().withLongOpt(LOPT_CERT)
                .withDescription(OPT_CERT_DESCRIPTION).create(OPT_CERT));
        options.addOption(OptionBuilder.hasArg().withLongOpt(LOPT_KEY)
                .withDescription(OPT_KEY_DESCRIPTION).create(OPT_KEY));
        options.addOption(OptionBuilder.hasArg().withLongOpt(LOPT_PASSWORD)
                .withDescription(OPT_PASSWORD_DESCRIPTION).create(OPT_PASSWORD));
        options.addOption(OptionBuilder.withLongOpt(LOPT_VERBOSE)
                .withDescription(OPT_VERBOSE_DESCRIPTION).create(OPT_VERBOSE));
        
        return options;
    }
    
    protected abstract Options defineCommandOptions();
    
    protected abstract void executeCommandService(CommandLine commandLine,
            ServiceClient serviceClient) throws CLIException, ParseException, RemoteException;

    protected void printVerboseMessage(String msg) {
        if (verboseMode)
            System.out.println(msg);
    }
    
    protected void printOutputMessage(String msg) {
        System.out.println(msg);
    }
    
    protected void printErrorMessage(String msg) {
        System.out.println(msg);
    }
    
}
