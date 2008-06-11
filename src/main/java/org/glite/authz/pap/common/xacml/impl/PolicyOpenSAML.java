package org.glite.authz.pap.common.xacml.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;
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

public class PolicyOpenSAML implements Policy {
	
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

	public boolean isPolicy() {
		return true;
	}

	public boolean isPolicyReference() {
		return false;
	}

	public boolean isPolicySet() {
		return false;
	}

	public boolean isPolicySetReference() {
		return false;
	}

	public boolean isReference() {
		return false;
	}

	public void setId(String policyId) {
		policy.setPolicyId(policyId);
	}

	public void toFile(File file) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD,"xml");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			tr.transform(new DOMSource(getDOM().getOwnerDocument()),new StreamResult(fos));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundXACMLException(e);
		}  catch (TransformerConfigurationException e) {
			throw new XACMLException(e);
		}  catch (TransformerException e) {
			throw new XACMLException(e);
		}
	}

	public void toFile(String fileName) {
		File file = new File(fileName);
		toFile(file);
	}

}
