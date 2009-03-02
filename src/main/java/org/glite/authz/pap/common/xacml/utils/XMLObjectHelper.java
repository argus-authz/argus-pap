package org.glite.authz.pap.common.xacml.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glite.authz.pap.common.exceptions.XMLObjectException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLObjectHelper<T extends XMLObject> {

	protected static final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
	//protected static final MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
	protected static final BasicParserPool ppMgr = new BasicParserPool();
	//protected static final UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
	private static final Object lock = new Object();

	static {
		ppMgr.setNamespaceAware(true);
	}

	protected XMLObjectHelper() {}

	public static XMLObject buildXMLObject(Element element) {
		XMLObject xmlObject;

		try {
			xmlObject = unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new XMLObjectException(e);
		}
		return xmlObject;
	}

	public static XMLObject buildXMLObject(InputStream inputStream) {
		Document doc = readDocument(inputStream);
		return buildXMLObject(doc.getDocumentElement());
	}

	public static XMLObject buildXMLObjectFromFile(File file) {
		Document doc = readDocument(file);
		return buildXMLObject(doc.getDocumentElement());
	}

	public static XMLObject buildXMLObjectFromFile(String fileName) {
		return buildXMLObjectFromFile(new File(fileName));
	}

	public static Element getDOM(XMLObject xmlObject) {
		Element element;

		try {
			element = marshall(xmlObject);
		} catch (MarshallingException e) {
			throw new XMLObjectException(e);
		}

		return element;
	}

	public static Element marshall(XMLObject xmlObject) throws MarshallingException {
		Element element;
		synchronized (lock) {
			MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
			Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
			element = marshaller.marshall(xmlObject);
		}
		return element;
	}

	public static XMLObject unmarshall(Element element) throws UnmarshallingException {
		XMLObject xmlObject;
		synchronized (lock) {
			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
			xmlObject = unmarshaller.unmarshall(element);
		}
		return xmlObject;
	}

	public static void toFile(File file, XMLObject xmlObject) {
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new XMLObjectException("Cannot write to file: " + file.getAbsolutePath(), e);
		}

		write(fos, xmlObject);
	}

	public static void toFile(String fileName, XMLObject xmlObject) {
		File file = new File(fileName);
		toFile(file, xmlObject);
	}

	public static String toString(XMLObject xmlObject) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		write(bos, xmlObject);
		return bos.toString();
	}

	public static void write(OutputStream outputStream, XMLObject xmlObject) {
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			tr.transform(new DOMSource(getDOM(xmlObject)), new StreamResult(outputStream));
		} catch (TransformerConfigurationException e) {
			throw new XMLObjectException(e);
		} catch (TransformerException e) {
			throw new XMLObjectException(e);
		}
	}

	private static Document readDocument(File file) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new XMLObjectException("File not found: " + file.getAbsolutePath(), e);
		}

		return readDocument(fileInputStream);
	}

	private static Document readDocument(InputStream inputStream) {
		Document doc;
		try {
			doc = ppMgr.parse(inputStream);
		} catch (XMLParserException e) {
			throw new XMLObjectException(e);
		}

		return doc;
	}

	@SuppressWarnings("unchecked")
	public T build(Element element) {
		T xmlObject = (T) buildXMLObject(element);
		return xmlObject;
	}

	public T buildFromFile(File file) {
		Document doc = readDocument(file);
		return build(doc.getDocumentElement());
	}

	public T buildFromFile(String fileName) {
		File file = new File(fileName);
		return buildFromFile(file);
	}

	public T clone(T xmlObject) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		write(bos, xmlObject);
		ByteArrayInputStream ios = new ByteArrayInputStream(bos.toByteArray());
		Document doc = readDocument(ios);
		return build(doc.getDocumentElement());
	}
}
