package org.glite.authz.pap.ui;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.impl.ServiceClientImplAxis;

public abstract class ServiceCLI {
    protected static final String DEFAULT_SERVICE_URL = "https://localhost:8443/";
    
    protected ServiceClient serviceLocator;
    
    public ServiceCLI() {
        serviceLocator = new ServiceClientImplAxis();
    }
    
    public ServiceCLI(ServiceClient serviceLocator) {
        this.serviceLocator = serviceLocator;
    }
    
    public abstract boolean execute(CommandLine commandLine, ServiceClient serviceClient) throws ParseException, RemoteException;
    
}
