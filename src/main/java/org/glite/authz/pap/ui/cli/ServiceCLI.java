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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.ServiceClientFactory;

public abstract class ServiceCLI {

    private static final String OPT_URL = "url";
    private static final String OPT_CERT = "cert";
    private static final String OPT_KEY = "key";
    private static final String OPT_PASSWORD = "password";
    private static final String DEFAULT_SERVICE_URL = "https://localhost:8443/pap/services/";
    private static final HelpFormatter helpFormatter = new HelpFormatter();

    protected static final String LOPT_PUBLIC = "public";
    protected static final String LOPT_PRIVATE = "private";
    protected static final String OPT_HELP = "h";
    protected static final String LOPT_HELP = "help";

    protected static final CommandLineParser parser = new GnuParser();
    private String usageText;
    private Options options = new Options();
    private Options commandOptions;
    private Options globalOptions = new Options();
    private String[] commandNameValues;
    private String descriptionText;
    private String longDescriptionText;
    private final ServiceClient serviceClient;

    @SuppressWarnings( { "static-access", "unchecked" })
    public ServiceCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {

        ServiceClientFactory serviceClientFactory = ServiceClientFactory.getServiceClientFactory();
        serviceClient = serviceClientFactory.createServiceClient();

        this.commandNameValues = commandNameValues;
        this.usageText = usage;
        this.descriptionText = description;
        this.longDescriptionText = longDescription;

        commandOptions = defineCommandOptions();
        if (commandOptions == null)
            commandOptions = new Options();

        commandOptions.addOption(OPT_HELP, LOPT_HELP, false, "Print this message");

        globalOptions.addOption(OptionBuilder.hasArg().withLongOpt("url").withDescription(
                "Specifies the target PAP endpoint to be contacted.").create(OPT_URL));
        globalOptions.addOption(OptionBuilder.hasArg().withLongOpt("cert").withDescription(
                "Specifies non-standard user certificate.").create(OPT_CERT));
        globalOptions.addOption(OptionBuilder.hasArg().withLongOpt("key").withDescription(
                "Specifies non-standard user private key.").create(OPT_KEY));
        globalOptions.addOption(OptionBuilder.hasArg().withLongOpt("password").withDescription(
                "Specifies a password that is used to decrypt the user's private key.").create(
                OPT_PASSWORD));

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

    public boolean execute(String[] args) throws ParseException, HelpMessageException, RemoteException {

        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption(OPT_HELP))
            throw new HelpMessageException();

        if (commandLine.hasOption(OPT_URL))
            serviceClient.setTargetEndpoint(commandLine.getOptionValue(OPT_URL));
        else
            serviceClient.setTargetEndpoint(DEFAULT_SERVICE_URL);
        if (commandLine.hasOption(OPT_CERT)) {
            serviceClient.setClientCertificate(commandLine.getOptionValue(OPT_CERT));
            System.out.println("Settato cert");
        }
        if (commandLine.hasOption(OPT_KEY))
            serviceClient.setClientPrivateKey(OPT_KEY);
        if (commandLine.hasOption(OPT_PASSWORD))
            serviceClient.setClientPrivateKeyPassword(OPT_PASSWORD);

        return executeCommandService(commandLine, serviceClient);

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

    protected abstract Options defineCommandOptions();

    protected abstract boolean executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException;

}
