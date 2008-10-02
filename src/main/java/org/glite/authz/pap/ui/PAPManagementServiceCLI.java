package org.glite.authz.pap.ui;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.PAPManagementService;

public class PAPManagementServiceCLI {
    
    private static final char OPT_PING = 'p';
    private static final char OPT_ADD_PAP = 'P';
    
    private static final String SERVICE_NAME = "pap/services/PAPManagementService";
    private static PAPManagementService papMgmtClient;
    
    protected static Options options = new Options();
    
    static {
        defineCommandLineOptions();
    }
    
    public static boolean execute(CommandLine commandLine, ServiceClient serviceClient) throws ParseException, RemoteException {
        
        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint() + SERVICE_NAME);
        
        if (commandLine.hasOption(OPT_PING))
            ping();
        else if (commandLine.hasOption(OPT_ADD_PAP))
            addTrustedPAP(commandLine.getOptionValues(OPT_ADD_PAP));
        else
            return false;
        
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Option> getOptions() {
        return options.getOptions();
    }
    
    private static void addTrustedPAP(String[] args) throws RemoteException {
        for (String s:args) {
            System.out.println("Inserted: " + s);
        }
        PAP pap = new PAP("alias_prova1", "endpoint_prova1", "dn_prova1");
        papMgmtClient.addTrustedPAP(pap);
    }
    
    @SuppressWarnings("static-access")
    private static void defineCommandLineOptions() {
        options.addOption(OptionBuilder.hasOptionalArgs().withLongOpt("ping").withDescription("Ping a PAP").create(OPT_PING));
        options.addOption(OptionBuilder.hasArgs().withLongOpt("add-pap").withDescription("Update policy").create(OPT_ADD_PAP));
    }

    private static void ping() throws RemoteException {
        System.out.println(papMgmtClient.ping());
    }

    private PAPManagementServiceCLI() { }

}
