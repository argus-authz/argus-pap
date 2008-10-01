package org.glite.authz.pap.ui;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.AxisPortType;
import org.glite.authz.pap.client.PortType;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.papmanagement.client.PAPManagementServiceClientFactory;
import org.glite.authz.pap.papmanagement.client.PAPManagementServicePortType;

public class PAPManagementServiceCLI extends ServiceCLI {
    
    private static final char OPT_PING = 'p';
    private static final char OPT_ADD_PAP = 'P';
    
    private static final String SERVICE_NAME = "pap/services/PAPManagementService";
    protected static Options options = new Options();
    
    static {
        defineCommandLineOptions();
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Option> getOptions() {
        return options.getOptions();
    }
    
    @SuppressWarnings("static-access")
    private static void defineCommandLineOptions() {
        options.addOption(OptionBuilder.hasOptionalArgs().withLongOpt("ping").withDescription("Ping a PAP").create(OPT_PING));
        options.addOption(OptionBuilder.hasArgs().withLongOpt("add-pap").withDescription("Update policy").create(OPT_ADD_PAP));
    }
    
    private PAPManagementServicePortType papMgmtClient;
    
    public PAPManagementServiceCLI() {
        this(new AxisPortType(DEFAULT_SERVICE_URL + SERVICE_NAME));
    }
    
    public PAPManagementServiceCLI(PortType portType) {
        super(portType);
        PAPManagementServiceClientFactory papMgmtFactory = PAPManagementServiceClientFactory.getPAPManagementServiceClientFactory();
        papMgmtClient = papMgmtFactory.createPAPManagementServiceClient().getPAPManagementServicePortType(this.portType.getTargetEndpoint());
    }
    
    @Override
    public boolean execute(CommandLine commandLine) throws ParseException, RemoteException {
        
        if (commandLine.hasOption(OPT_PING))
            ping();
        else if (commandLine.hasOption(OPT_ADD_PAP))
            addTrustedPAP(commandLine.getOptionValues(OPT_ADD_PAP));
        else
            return false;
        
        return true;
    }

    protected void addTrustedPAP(String[] args) throws RemoteException {
        for (String s:args) {
            System.out.println("Inserted: " + s);
        }
        PAP pap = new PAP("alias_prova1", "endpoint_prova1", "dn_prova1");
        papMgmtClient.addTrustedPAP(pap);
    }

    protected void ping() throws RemoteException {
        System.out.println(papMgmtClient.ping());
    }

}
