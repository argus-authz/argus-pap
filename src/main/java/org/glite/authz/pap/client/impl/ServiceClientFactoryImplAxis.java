package org.glite.authz.pap.client.impl;

import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.client.ServiceClientFactory;

public class ServiceClientFactoryImplAxis extends ServiceClientFactory {

    @Override
    public ServiceClient createServiceClient() {
        return new ServiceClientImplAxis();
    }

}
