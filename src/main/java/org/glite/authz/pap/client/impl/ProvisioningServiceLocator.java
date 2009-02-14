/**************************************************************************

 Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 File : ProvisioningServicePortTypeImpl.java

 Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.client.impl;

import java.net.URL;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import org.apache.axis.client.Service;
import org.glite.authz.pap.services.provisioning.axis_skeletons.Provisioning;

public class ProvisioningServiceLocator {
    
    private final Service service;

    public ProvisioningServiceLocator() {
        service = new Service();
    }
    
    public Provisioning getProvisioningService(URL url) throws ServiceException {
        return new ProvisioningServiceStub(url, service);
    }
    
    public TypeMappingRegistry getTypeMappingRegistry() {
        return service.getTypeMappingRegistry();
    }
}
