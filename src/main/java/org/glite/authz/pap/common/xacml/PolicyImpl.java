package org.glite.authz.pap.common.xacml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PolicyImpl implements Policy {
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
	
	public String getId() {
		return attributeId.getNodeValue();
	}

	public void printXACMLDOMToFile(File file) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD,"xml");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			tr.transform(new DOMSource(this.policyDOM),new StreamResult(fos));
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

	public void setId(String policyId) {
		attributeId.setNodeValue(policyId);
	}

	public Node getDOM() {
		return this.policyDOM;
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
	
	private void init(Document doc) {
		policyDOM = doc.getDocumentElement();
		attributeId = getDOMPolicyAttributeId(doc);
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
