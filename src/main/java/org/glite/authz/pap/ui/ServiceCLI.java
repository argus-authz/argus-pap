package org.glite.authz.pap.ui;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.AxisPortType;
import org.glite.authz.pap.client.PortType;

public abstract class ServiceCLI {
    protected static final String DEFAULT_SERVICE_URL = "https://localhost:8443/";
    
    protected PortType portType;
    
    public ServiceCLI() {
        portType = new AxisPortType();
    }
    
    public ServiceCLI(PortType portType) {
        this.portType = portType;
    }
    
    public abstract boolean execute(CommandLine commandLine) throws ParseException, RemoteException;
    
}
