package org.glite.authz.pap.client;

import org.glite.authz.pap.client.impl.ServiceClientFactoryImplAxis;


public abstract class ServiceClientFactory {
    
    public static ServiceClientFactory getServiceClientFactory() {
        return new ServiceClientFactoryImplAxis();
    }

    public abstract ServiceClient createServiceClient();
    
}
