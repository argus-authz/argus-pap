package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.utils.xacml.XACMLException;
import org.glite.authz.pap.common.xacml.Policy;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PolicyOpenSAML extends Policy {
	
	private XMLObjectBuilder<PolicyType> policyBuilder;
	private PolicyType policy;
	
	public PolicyOpenSAML(Element element) {
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
		try {
			policy = (PolicyType) unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new XACMLException(e);
		}
	}
	
	public PolicyOpenSAML(PolicyType openSAMLPolicy) {
		policy = openSAMLPolicy;
	}
	
	@SuppressWarnings("unchecked")
	public PolicyOpenSAML(String policyId, String ruleCombinerAlgorithmId) {
		policyBuilder = Configuration.getBuilderFactory().getBuilder(org.opensaml.xacml.policy.PolicyType.DEFAULT_ELEMENT_NAME);
		policy = policyBuilder.buildObject(org.opensaml.xacml.policy.PolicyType.DEFAULT_ELEMENT_NAME);
		policy.setPolicyId(policyId);
		policy.setTarget(new TargetOpenSAML().getOpenSAMLTargetType());
		policy.setRuleCombiningAlgoId(ruleCombinerAlgorithmId);
	}

	public Node getDOM() {
		MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(policy);
		try {
			marshaller.marshall(policy);
		} catch (MarshallingException e) {
			throw new XACMLException(e);
		}
		return policy.getDOM();
	}

	public String getId() {
		return policy.getPolicyId();
	}

	public void setId(String policyId) {
		policy.setPolicyId(policyId);
	}

}
