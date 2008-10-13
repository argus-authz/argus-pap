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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.glite.authz.pap.policymanagement.PolicyManagementService;
import org.glite.authz.pap.provisioning.axis.DeserializerFactory;
import org.glite.authz.pap.provisioning.axis.SerializerFactory;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class PolicyManagementServiceClientImpl implements
		PolicyManagementService {

	private final Service service;
	private final String serviceURL;

	public PolicyManagementServiceClientImpl(String serviceURL, Service service) {
		this.serviceURL = serviceURL;
		this.service = service;
	}

	public PolicyType getPolicy(String policyId) throws RemoteException {
		Call call = createCall("getPolicy");

		call.registerTypeMapping(PolicyType.class,
				PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		PolicyType policy = (PolicyType) call.invoke(new Object[] { policyId });

		return policy;
	}

	public PolicySetType getPolicySet(String policySetId)
			throws RemoteException {
		Call call = createCall("getPolicySet");

		call.registerTypeMapping(PolicySetType.class,
				PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		return (PolicySetType) call.invoke(new Object[] { policySetId });
	}

	public boolean hasPolicy(String policyId) throws RemoteException {
		Call call = createCall("hasPolicy");
		String dirtyTrick = (String) call.invoke(new Object[] { policyId });
		return Boolean.parseBoolean(dirtyTrick);
	}

	public boolean hasPolicySet(String policySetId) throws RemoteException {
		Call call = createCall("hasPolicySet");
		String dirtyTrick = (String) call.invoke(new Object[] { policySetId });
		return Boolean.parseBoolean(dirtyTrick);
	}

	public List<PolicyType> listPolicies() throws RemoteException {
		Call call = createCall("listPolicies");

		call.registerTypeMapping(PolicyType.class,
				PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		Object object = call.invoke(new Object[] {});
		
		List<PolicyType> policyList = getListOfPolicies(object);
		
		return policyList;
	}

	public List<PolicyType> listPolicies(String papId) throws RemoteException {
		Call call = createCall("listPolicies");

		call.registerTypeMapping(PolicyType.class,
				PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		Object object = call.invoke(new Object[] { papId });
		
		List<PolicyType> policyList = getListOfPolicies(object);
		
		return policyList;
	}

	public List<PolicySetType> listPolicySets() throws RemoteException {
		Call call = createCall("listPolicySets");

		call.registerTypeMapping(PolicySetType.class,
				PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		Object object = call.invoke(new Object[] {});
		
		List<PolicySetType> policySetList = getListOfPolicySets(object);
		
		return policySetList;
	}

	public List<PolicySetType> listPolicySets(String papId)
			throws RemoteException {
		Call call = createCall("listPolicySets");

		call.registerTypeMapping(PolicySetType.class,
				PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		Object object = call.invoke(new Object[] { papId });
		
		List<PolicySetType> policySetList = getListOfPolicySets(object);
		
		return policySetList;
	}

	public void removePolicy(String policyId) throws RemoteException {
		Call call = createCall("removePolicy");
		call.invoke(new Object[] { policyId });
	}

	public void removePolicySet(String policySetId) throws RemoteException {
		Call call = createCall("removePolicySet");
		call.invoke(new Object[] { policySetId });
	}

	public String storePolicy(String idPrefix, PolicyType policy)
			throws RemoteException {
		Call call = createCall("storePolicy");

		call.registerTypeMapping(PolicyType.class,
				PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		return (String) call.invoke(new Object[] { idPrefix, policy });
	}

	public String storePolicySet(String idPrefix, PolicySetType policySet)
			throws RemoteException {
		Call call = createCall("storePolicySet");

		call.registerTypeMapping(PolicySetType.class,
				PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		return (String) call.invoke(new Object[] { idPrefix, policySet });
	}

	public void updatePolicy(PolicyType policy) throws RemoteException {
		Call call = createCall("updatePolicy");

		call.registerTypeMapping(PolicyType.class,
				PolicyType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		call.invoke(new Object[] { policy });
	}

	public void updatePolicySet(PolicySetType policySet) throws RemoteException {
		Call call = createCall("updatePolicySet");

		call.registerTypeMapping(PolicySetType.class,
				PolicySetType.DEFAULT_ELEMENT_NAME, new SerializerFactory(),
				new DeserializerFactory());

		call.invoke(new Object[] { policySet });
	}

	private Call createCall(String operationName) {
		Call call;
		try {
			call = (Call) service.createCall();
		} catch (ServiceException e) {
			throw new RuntimeException("Error", e);
		}

		call.setTargetEndpointAddress(serviceURL);
		call.setOperationName(new QName(
				"urn:org:glite:authz:pap:policymanagement", operationName));

		call.setOperationStyle(Style.RPC);
		call.setOperationUse(Use.LITERAL);

		return call;
	}
	
	@SuppressWarnings("unchecked")
	private List<PolicyType> getListOfPolicies(Object object) {
		
		if (object == null)
			return new ArrayList<PolicyType>(0);
		
		List<PolicyType> policyList;
		
		if (object instanceof PolicyType) {
			policyList = new ArrayList<PolicyType>(1);
			policyList.add((PolicyType) object);
		} else
			policyList = ((List<PolicyType>) object);
		
		return (List<PolicyType>) policyList;
	}
	
	@SuppressWarnings("unchecked")
	private List<PolicySetType> getListOfPolicySets(Object object) {
		
		if (object == null)
			return new ArrayList<PolicySetType>(0);
		
		List<PolicySetType> policyList;
		
		if (object instanceof PolicyType) {
			policyList = new ArrayList<PolicySetType>(1);
			policyList.add((PolicySetType) object);
		} else
			policyList = ((List<PolicySetType>) object);
		
		return (List<PolicySetType>) policyList;
	}

}
