package org.glite.authz.pap.common.xacml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glite.authz.pap.common.utils.xacml.FileNotFoundXACMLException;
import org.glite.authz.pap.common.utils.xacml.XACMLException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class PolicySetBuilder {
	
	public abstract PolicySet build(String policySetId, String policyCombinerAlgorithmId);
	
	public abstract PolicySet build(Document doc);

	public PolicySet buildFromFile(File file) {
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
		return build(doc);
	}
	
	public PolicySet buildFromFile(String fileName) {
		File file = new File(fileName);
		return buildFromFile(file);
	}
}
