package org.glite.authz.pap.common.utils.xacml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

import org.glite.authz.pap.common.exceptions.FileNotFoundXACMLException;
import org.glite.authz.pap.common.exceptions.XACMLException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xacml.XACMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XACMLHelper<XACMLObjectType extends XACMLObject> {

    @SuppressWarnings("unchecked")
    public XACMLObjectType build(Element element) {
	UnmarshallerFactory unmarshallerFactory = Configuration
		.getUnmarshallerFactory();
	Unmarshaller unmarshaller = unmarshallerFactory
		.getUnmarshaller(element);
	try {
	    return (XACMLObjectType) unmarshaller.unmarshall(element);
	} catch (UnmarshallingException e) {
	    throw new XACMLException(e);
	}
    }

    public XACMLObjectType buildFromFile(File file) {
	if ((!file.exists()) || (!file.canRead())) {
	    throw new FileNotFoundXACMLException();
	}
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	Document doc = null;
	try {
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    doc = db.parse(file);
	} catch (ParserConfigurationException e) {
	    throw new XACMLException(e);
	} catch (SAXException e) {
	    throw new XACMLException(e);
	} catch (IOException e) {
	    throw new XACMLException(e);
	}
	return build(doc.getDocumentElement());
    }

    public XACMLObjectType buildFromFile(String fileName) {
	File file = new File(fileName);
	return buildFromFile(file);
    }

    public Node getDOM(XACMLObjectType xacmlObject) {
	MarshallerFactory marshallerFactory = Configuration
		.getMarshallerFactory();
	Marshaller marshaller = marshallerFactory.getMarshaller(xacmlObject);
	try {
	    marshaller.marshall(xacmlObject);
	} catch (MarshallingException e) {
	    throw new XACMLException(e);
	}
	return xacmlObject.getDOM();
    }

    public void toFile(File file, XACMLObjectType xacmlObject) {

	// Use write()
	FileOutputStream fos;
	try {
	    fos = new FileOutputStream(file);
	    Transformer tr = TransformerFactory.newInstance().newTransformer();
	    tr.setOutputProperty(OutputKeys.INDENT, "yes");
	    tr.setOutputProperty(OutputKeys.METHOD, "xml");
	    tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
		    "3");
	    tr.transform(new DOMSource(getDOM(xacmlObject).getOwnerDocument()),
		    new StreamResult(fos));
	} catch (FileNotFoundException e) {
	    throw new FileNotFoundXACMLException(e);
	} catch (TransformerConfigurationException e) {
	    throw new XACMLException(e);
	} catch (TransformerException e) {
	    throw new XACMLException(e);
	}
    }

    public void toFile(String fileName, XACMLObjectType xacmlObject) {
	File file = new File(fileName);
	toFile(file, xacmlObject);
    }

    public void write(OutputStream outputStream) {
	// TODO
    }

}
