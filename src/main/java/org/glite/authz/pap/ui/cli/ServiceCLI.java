package org.glite.authz.pap.ui.cli;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.glite.authz.pap.client.ServiceClient;

public abstract class ServiceCLI {
    
    protected static final CommandLineParser parser = new GnuParser();
    protected static final HelpFormatter helpFormatter = new HelpFormatter();
    protected String commandName = null;
    
    public String getCommandName() {
        return commandName;
    }
    
    public abstract boolean execute(String[] args, ServiceClient serviceClient);

}
