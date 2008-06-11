package org.glite.authz.pap.common.xacml.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glite.authz.pap.common.xacml.AbstractPolicy;
import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PolicySetOpenSAML implements PolicySet {

	private XMLObjectBuilder<PolicySetType> policySetBuilder;
	private PolicySetType policySet;
	
	public PolicySetOpenSAML(Element element) {
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
		try {
			policySet = (PolicySetType) unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new XACMLException(e);
		}
	}
	
	public PolicySetOpenSAML(PolicySetType openSAMLPolicySet) {
		policySet = openSAMLPolicySet;
	}
	
	@SuppressWarnings("unchecked")
	public PolicySetOpenSAML(String policySetId, String policyCombinerAlgorithmId) {
		policySetBuilder = Configuration.getBuilderFactory().getBuilder(org.opensaml.xacml.policy.PolicySetType.DEFAULT_ELEMENT_NAME);
		policySet = policySetBuilder.buildObject(org.opensaml.xacml.policy.PolicySetType.DEFAULT_ELEMENT_NAME);
		policySet.setPolicySetId(policySetId);
		policySet.setTarget(new TargetOpenSAML().getOpenSAMLTargetType());
		policySet.setPolicyCombiningAlgoId(policyCombinerAlgorithmId);
	}

	public void deletePolicyReference(String policyId) {
		// TODO Auto-generated method stub

	}

	public void deletePolicySetReference(String policySetId) {
	}

	public Node getDOM() {
		MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(policySet);
		try {
			marshaller.marshall(policySet);
		} catch (MarshallingException e) {
			throw new XACMLException(e);
		}
		return policySet.getDOM();
	}

	public AbstractPolicy getFirstXACMLObjectChildren() {
		List<AbstractPolicy> children = getOrderedChildren();
		AbstractPolicy child = null;
		if (!children.isEmpty()) {
			child = children.get(0);
		}
		return child;
	}

	public String getId() {
		return policySet.getPolicySetId();
	}

	public AbstractPolicy getLastXACMLObjectChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumberOfXACMLObjectChildren() {
		return getOrderedChildren().size();
	}

	public List<AbstractPolicy> getOrderedListOfXACMLObjectChildren() {
		return getOrderedChildren();
	}

	public void insertPolicyReferenceAsFirst(String value) {
		policySet.getPolicyIdReferences().add(0, new ReferenceIdOpenSAML(IdReference.Type.POLICYIDREFERENCE, value).getOpenSAMLObject());
	}

	public void insertPolicyReferenceAsLast(String value) {
		policySet.getPolicyIdReferences().add(new ReferenceIdOpenSAML(IdReference.Type.POLICYIDREFERENCE, value).getOpenSAMLObject());
	}

	public void insertPolicySetReferenceAsFirst(String value) {
		policySet.getPolicySetIdReferences().add(0, new ReferenceIdOpenSAML(IdReference.Type.POLICYSETIDREFERENCE, value).getOpenSAMLObject());
	}

	public void insertPolicySetReferenceAsLast(String value) {
		policySet.getPolicySetIdReferences().add(new ReferenceIdOpenSAML(IdReference.Type.POLICYSETIDREFERENCE, value).getOpenSAMLObject());
	}

	public boolean isPolicy() {
		return false;
	}

	public boolean isPolicyReference() {
		return false;
	}

	public boolean isPolicySet() {
		return true;
	}

	public boolean isPolicySetReference() {
		return false;
	}

	public boolean isReference() {
		return false;
	}
	
	public boolean policyReferenceIdExists(String id) {
		return referenceExists(id, policySet.getPolicyIdReferences());
	}

	public boolean policySetReferenceIdExists(String id) {
		return referenceExists(id, policySet.getPolicySetIdReferences());
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

	public boolean referenceIdExists(String id) {
		List<AbstractPolicy> children = getOrderedChildren();
		for (AbstractPolicy child:children) {
			if (child.isReference()) {
				if (((IdReference) child).getValue().equals(id)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setId(String policySetId) {
		policySet.setPolicySetId(policySetId);
	}

	private List<AbstractPolicy> getOrderedChildren() {
		List<AbstractPolicy> xacmlObjectChildren = new LinkedList<AbstractPolicy>();
		List<XMLObject> children = policySet.getOrderedChildren();
		for (XMLObject child:children) {
			if (child instanceof IdReferenceType) {
				xacmlObjectChildren.add(new ReferenceIdOpenSAML((IdReferenceType) child));
			} else if (child instanceof PolicySetType) {
				xacmlObjectChildren.add(new PolicySetOpenSAML((PolicySetType) child));
			} else if (child instanceof PolicyType) {
				//xacmlObjectChildren.add(new PolicySetOpenSAML((PolicySetType) child));
			}
		}
		return xacmlObjectChildren;
	}

	private boolean referenceExists(String id, List<IdReferenceType> list) {
		for (IdReferenceType ref:list) {
			if (id.equals(ref.getValue())) {
				return true;
			}
		}
		return false;
	}
}
