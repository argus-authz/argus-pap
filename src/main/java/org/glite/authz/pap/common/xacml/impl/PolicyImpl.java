package org.glite.authz.pap.common.xacml.impl;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.xacml.exceptions.InvalidPolicySet;
import org.glite.authz.pap.common.xacml.exceptions.XACMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PolicyImpl extends Policy {
	private Node policyDOM;
	private Node attributeId;

	public PolicyImpl(Document doc) {
		init(doc);
	}
	
	/**
	 * @param file
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException XML parse error
	 */
	public PolicyImpl(File file) {
		init(readXACMLFromFile(file));
	}
	
	
	/**
	 * @param fileName
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException XML parse error
	 */
	public PolicyImpl(String fileName) {
		init(readXACMLFromFile(fileName));
	}
	
	public Node getDOM() {
		return this.policyDOM;
	}

	public String getId() {
		return attributeId.getNodeValue();
	}

	public void setId(String policyId) {
		attributeId.setNodeValue(policyId);
	}

	private Node getDOMPolicyAttributeId(Document doc) {
		Node attributeId = null;
		NodeList nodeList = doc.getChildNodes();
		Node element = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			element = nodeList.item(i);
			String nodeName = element.getLocalName();
			if ("Policy".equals(nodeName)) {
				attributeId = element.getAttributes().getNamedItem("PolicyId");
				break;
			}
		}
		if ((element == null) || (attributeId == null)) {
			throw new InvalidPolicySet();
		}
		return attributeId;
	}
	
	private void init(Document doc) {
		policyDOM = doc.getDocumentElement();
		attributeId = getDOMPolicyAttributeId(doc);
	}
	
	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundXACMLException
	 * @throws XACMLException XML parse error.
	 */
	private Document readXACMLFromFile(File file) {
		if (!file.exists()) {
			throw new FileNotFoundXACMLException("File does not exists: " + file.getAbsolutePath());
		}
		if (!file.canRead()) {
			throw new FileNotFoundXACMLException("Access denied");
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
