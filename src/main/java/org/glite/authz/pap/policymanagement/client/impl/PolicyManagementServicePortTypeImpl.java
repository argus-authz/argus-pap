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

package org.glite.authz.pap.policymanagement.client.impl;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.glite.authz.pap.client.AxisPortType;
import org.glite.authz.pap.policymanagement.client.PolicyManagementServicePortType;
import org.glite.authz.pap.provisioning.axis.DeserializerFactory;
import org.glite.authz.pap.provisioning.axis.SerializerFactory;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class PolicyManagementServicePortTypeImpl extends AxisPortType implements PolicyManagementServicePortType {

    private Service service;
    
    public PolicyManagementServicePortTypeImpl(String url) {
        setTargetEndpoint(url);
        service = getService();
    }

    public PolicyType getPolicy(String policyId) throws RemoteException {
        Call call = createCall("getPolicy");

        call.registerTypeMapping(PolicyType.class, PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        PolicyType policy = (PolicyType) call.invoke(new Object[] { policyId });
        
        return policy;
    }
    
    public PolicySetType getPolicySet(String policySetId) throws RemoteException {
        Call call = createCall("getPolicySet");

        call.registerTypeMapping(PolicySetType.class, PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());
        
        return (PolicySetType) call.invoke(new Object[] { policySetId });
    }
    
    @SuppressWarnings("unchecked")
    public List<PolicyType> listPolicies() throws RemoteException {
        Call call = createCall("listPolicies");

        call.registerTypeMapping(PolicyType.class, PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        return (List<PolicyType>) call.invoke(new Object[] {});
    }

    @SuppressWarnings("unchecked")
    public List<PolicyType> listPolicies(String papId) throws RemoteException {
        Call call = createCall("listPolicies");

        call.registerTypeMapping(PolicyType.class, PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        return (List<PolicyType>) call.invoke(new Object[] { papId });
    }

    @SuppressWarnings("unchecked")
    public List<PolicySetType> listPolicySets() throws RemoteException {
        Call call = createCall("listPolicySets");

        call.registerTypeMapping(PolicySetType.class, PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        return (List<PolicySetType>) call.invoke(new Object[] {});
    }

    @SuppressWarnings("unchecked")
    public List<PolicySetType> listPolicySets(String papId) throws RemoteException {
        Call call = createCall("listPolicySets");

        call.registerTypeMapping(PolicySetType.class, PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        return (List<PolicySetType>) call.invoke(new Object[] { papId });
    }

    public void removePolicy(String policyId) throws RemoteException {
        Call call = createCall("removePolicy");
        call.invoke(new Object[] { policyId });
    }

    public void removePolicySet(String policySetId) throws RemoteException {
        Call call = createCall("removePolicySet");
        call.invoke(new Object[] { policySetId });
    }
    
    public String storePolicy(String idPrefix, PolicyType policy) throws RemoteException {
        Call call = createCall("storePolicy");

        call.registerTypeMapping(PolicyType.class, PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        return (String) call.invoke(new Object[] { idPrefix, policy });
    }

    public String storePolicySet(String idPrefix, PolicySetType policySet) throws RemoteException {
        Call call = createCall("storePolicySet");

        call.registerTypeMapping(PolicySetType.class, PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        return (String) call.invoke(new Object[] { idPrefix, policySet });
    }

    public void updatePolicy(PolicyType policy) throws RemoteException {
        Call call = createCall("updatePolicy");

        call.registerTypeMapping(PolicyType.class, PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());
        
        call.invoke(new Object[] { policy } );
    }

    public void updatePolicySet(PolicySetType policySet) throws RemoteException {
        Call call = createCall("updatePolicySet");

        call.registerTypeMapping(PolicySetType.class, PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(), new DeserializerFactory());

        call.invoke(new Object[] { policySet });
    }

    private Call createCall(String operationName) {
        Call call;
        try {
            call = (Call) service.createCall();
        } catch (ServiceException e) {
            throw new RuntimeException("Error", e);
        }

        call.setTargetEndpointAddress(getTargetEndpoint());
        call.setOperationName(new QName("urn:org:glite:authz:pap:policymanagement", operationName));

        call.setOperationStyle(Style.RPC);
        call.setOperationUse(Use.LITERAL);
        
        return call;
    }

}
