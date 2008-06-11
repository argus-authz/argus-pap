package org.glite.authz.pap.common.xacml.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.AbstractPolicy;
import org.glite.authz.pap.common.xacml.IdReference.Type;
import org.glite.authz.pap.common.xacml.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.xacml.exceptions.InvalidPolicySet;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PolicySetImpl implements PolicySet {

	private Node policySetDOM;
	private Node attributeId;
	
	public PolicySetImpl(Document doc) {
		init(doc);
	}
	
	/**
	 * @param file
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException XML parse error
	 */
	public PolicySetImpl(File file) {
		init(readXACMLFromFile(file));
	}
	
	
	/**
	 * @param fileName
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException XML parse error
	 */
	public PolicySetImpl(String fileName) {
		init(readXACMLFromFile(fileName));
	}
	
	public void deletePolicyReference(String policyId) {
		List<AbstractPolicy> children = getOrderedListOfXACMLObjectChildren();
		for (AbstractPolicy child:children) {
			if (child.isPolicyReference()) {
				if (policyId.equals(((IdReference) child).getValue())) {
					policySetDOM.removeChild(child.getDOM());
				}
			}
		}
	}
	
	public void deletePolicySetReference(String policySetId) {
		List<AbstractPolicy> children = getOrderedListOfXACMLObjectChildren();
		for (AbstractPolicy child:children) {
			if (child.isPolicySetReference()) {
				if (policySetId.equals(((IdReference) child).getValue())) {
					policySetDOM.removeChild(child.getDOM());
				}
			}
		}
	}

	public Node getDOM() {
		return this.policySetDOM;
	}

	public AbstractPolicy getFirstXACMLObjectChildren() {
		List<AbstractPolicy> childrenList = getOrderedListOfXACMLObjectChildren();
		if (childrenList.isEmpty()) {
			return null;
		} 
		return childrenList.get(0);
	}

	public String getId() {
		return attributeId.getNodeValue();
	}

	public AbstractPolicy getLastXACMLObjectChildren() {
		List<AbstractPolicy> children = getOrderedListOfXACMLObjectChildren();
		if (children.isEmpty()) {
			return null;
		}
		return children.get(children.size()-1);
	}

	public int getNumberOfXACMLObjectChildren() {
		return getOrderedListOfXACMLObjectChildren().size();
	}
	
	public List<AbstractPolicy> getOrderedListOfXACMLObjectChildren() {
		List<AbstractPolicy> result = new LinkedList<AbstractPolicy>();
		NodeList nodeList = policySetDOM.getChildNodes();
		Node node = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			node = nodeList.item(i);
			String nodeName = node.getLocalName();
			if (nodeName != null) {
				if ("PolicySet".equals(nodeName)) {
					result.add(new PolicySetImpl(node.getOwnerDocument()));
				} else if ("Policy".equals(nodeName)) {
					// TODO
				} else if ("PolicySetIdReference".equals(nodeName)) {
					result.add(new ReferenceIdImpl(IdReference.Type.POLICYSETIDREFERENCE, node.getTextContent(), node));
				} else if ("PolicyIdReference".equals(nodeName)) {
					result.add(new ReferenceIdImpl(IdReference.Type.POLICYIDREFERENCE, node.getTextContent(), node));
				}
			}
		}
		return result;
	}

	public void insertPolicyReferenceAsFirst(String value) {
		if (policyReferenceIdExists(value)) {
			throw new XACMLException("Reference already exists");
		}
		Node firstChild = null;
		AbstractPolicy child = getFirstXACMLObjectChildren();
		if (child != null) {
			firstChild = getFirstXACMLObjectChildren().getDOM();
		}
		Document doc = policySetDOM.getOwnerDocument();
		Element ref = doc.createElementNS(null, "PolicyIdReference");
		ref.setTextContent(value);
		if (firstChild == null) {
			policySetDOM.appendChild(ref);
		} else {
			policySetDOM.insertBefore(ref, firstChild);
		}
	}

	public void insertPolicyReferenceAsLast(String value) {
		// TODO Auto-generated method stub
		
	}
	
	public void insertPolicySetReferenceAsFirst(String value) {
		if (policySetReferenceIdExists(value)) {
			throw new XACMLException("Reference already exists");
		}
		Node firstChild = null;
		AbstractPolicy child = getFirstXACMLObjectChildren();
		if (child != null) {
			firstChild = getFirstXACMLObjectChildren().getDOM();
		}
		Document doc = policySetDOM.getOwnerDocument();
		Element ref = doc.createElementNS(null, "PolicySetIdReference");
		ref.setTextContent(value);
		if (firstChild == null) {
			policySetDOM.appendChild(ref);
		} else {
			policySetDOM.insertBefore(ref, firstChild);
		}
	}

	public void insertPolicySetReferenceAsLast(String value) {
		// TODO Auto-generated method stub
		
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
		List<AbstractPolicy> children = getOrderedListOfXACMLObjectChildren();
		for (AbstractPolicy child:children) {
			if (child.isPolicyReference()) {
				if (id.equals(((IdReference) child).getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean policySetReferenceIdExists(String id) {
		List<AbstractPolicy> children = getOrderedListOfXACMLObjectChildren();
		for (AbstractPolicy child:children) {
			if (child.isPolicySetReference()) {
				if (id.equals(((IdReference) child).getValue())) {
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
			tr.transform(new DOMSource(this.policySetDOM),new StreamResult(fos));
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
		List<AbstractPolicy> children = getOrderedListOfXACMLObjectChildren();
		for (AbstractPolicy child:children) {
			if (child.isReference()) {
				if (id.equals(((IdReference) child).getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	public void setId(String policySetId) {
		attributeId.setNodeValue(policySetId);
	}

	private Node getDOMPolicySetAttributeId(Document doc) {
		Node attributeId = null;
		NodeList nodeList = doc.getChildNodes();
		Node element = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			element = nodeList.item(i);
			String nodeName = element.getLocalName();
			if ("PolicySet".equals(nodeName)) {
				attributeId = element.getAttributes().getNamedItem("PolicySetId");
				break;
			}
		}
		if ((element == null) || (attributeId == null)) {
			throw new InvalidPolicySet();
		}
		return attributeId;
	}

	private void init(Document doc) {
		policySetDOM = doc.getDocumentElement();
		attributeId = getDOMPolicySetAttributeId(doc);
	}

	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException XML parse error.
	 */
	private Document readXACMLFromFile(File file) {
		if (!file.exists()) {
			throw new FileNotFoundXACMLException();
		}
		if (!file.canRead()) {
			throw new FileNotFoundXACMLException();
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch(ParserConfigurationException e) {
			throw new XACMLException(e);
		} catch(SAXException e) {
			throw new XACMLException(e);
		} catch(IOException e) {
			throw new XACMLException(e);
		}
		return doc;
	}

	/**
	 * @param fileName
	 * @return
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException XML parse error
	 */
	private Document readXACMLFromFile(String fileName) {
		File file = new File(fileName);
		return readXACMLFromFile(file);
	}
}
