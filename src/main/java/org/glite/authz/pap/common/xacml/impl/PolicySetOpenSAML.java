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

import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.XACMLObject;
import org.glite.authz.pap.common.xacml.IdReference.Type;
import org.glite.authz.pap.common.xacml.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PolicySetOpenSAML implements PolicySet {
	
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

	public void deletePolicyReference(String policyId) {
		// TODO Auto-generated method stub

	}

	public void deletePolicySetReference(String policySetId) {
		int index = getIndexOfPolicySetReference(policySetId);
		List<XMLObject> children = policySet.getOrderedChildren();
		children.remove(index);
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

	public XACMLObject getFirstXACMLObjectChildren() {
		List<XACMLObject> children = getOrderedChildren();
		XACMLObject child = null;
		if (!children.isEmpty()) {
			child = children.get(0);
		}
		return child;
	}

	public String getId() {
		return policySet.getPolicySetId();
	}

	public XACMLObject getLastXACMLObjectChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumberOfXACMLObjectChildren() {
		return getOrderedChildren().size();
	}

	public List<XACMLObject> getOrderedListOfXACMLObjectChildren() {
		return getOrderedChildren();
	}

	public void insertPolicyReferenceAsFirst(String value) {
		// TODO Auto-generated method stub

	}

	public void insertPolicySetReferenceAsFirst(String value) {
		int index = getIndexOfFirstChildren();
		//index = 0;
		System.out.println("WWWW " + index);
		List<XMLObject> xmlChildren = policySet.getOrderedChildren();
		xmlChildren.add(index, new ReferenceIdOpenSAML(IdReference.Type.POLICYSETIDREFERENCE, value).getOpenSAMLObject());
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
		List<XACMLObject> children = getOrderedChildren();
		for (XACMLObject child:children) {
			if (child.isPolicyReference()) {
				if (((IdReference) child).getValue().equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean policySetReferenceIdExists(String id) {
		List<XACMLObject> children = getOrderedChildren();
		for (XACMLObject child:children) {
			if (child.isPolicySetReference()) {
				if (((IdReference) child).getValue().equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public void printXACMLDOMToFile(File file) {
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

	public void printXACMLDOMToFile(String fileName) {
		File file = new File(fileName);
		printXACMLDOMToFile(file);
	}

	public boolean referenceIdExists(String id) {
		List<XACMLObject> children = getOrderedChildren();
		for (XACMLObject child:children) {
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
	
	private List<XACMLObject> getOrderedChildren() {
		List<XACMLObject> xacmlObjectChildren = new LinkedList<XACMLObject>();
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
	
	private int getIndexOfFirstChildren() {
		int index = 0;
		List<XMLObject> children = policySet.getOrderedChildren();
		for (XMLObject child : children) {
			if ((child instanceof IdReferenceType)
					|| (child instanceof PolicySetType)
					|| (child instanceof PolicyType)) {
				break;
			}
			index++;
		}
		return index;
	}
	
	private int getIndexOfPolicySetReference(String id) {
		int index = 0;
		List<XMLObject> children = policySet.getOrderedChildren();
		for (XMLObject child : children) {
			if (child instanceof IdReferenceType) {
				if (((IdReferenceType) child).getElementQName().equals(IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME)) {
					if (((IdReferenceType) child).getValue().equals(id)) {
						break;
					}
				}
			}
			index++;
		}
		return index;
	}
}
