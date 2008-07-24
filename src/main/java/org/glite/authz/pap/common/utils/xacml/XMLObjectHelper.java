package org.glite.authz.pap.common.utils.xacml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    protected static final MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
    protected static final UnmarshallerFactory unmarshallerFactory = Configuration
            .getUnmarshallerFactory();
    protected static final BasicParserPool ppMgr = new BasicParserPool();

    static {
        ppMgr.setNamespaceAware(true);
    }

    public static XMLObject buildXMLObject(Element element) {
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        XMLObject xmlObject;

        try {
            xmlObject = unmarshaller.unmarshall(element);
        } catch (UnmarshallingException e) {
            throw new XMLObjectException(e);
        }

        return xmlObject;
    }

    public static XMLObject buildXMLObjectFromFile(File file) {
        Document doc = readDocumentFromFile(file);
        return buildXMLObject(doc.getDocumentElement());
    }

    public static Element getDOM(XMLObject xmlObject) {
        Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);

        try {
            marshaller.marshall(xmlObject);
        } catch (MarshallingException e) {
            throw new XMLObjectException(e);
        }

        return xmlObject.getDOM();
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

    private static Document readDocumentFromFile(File file) {
        Document doc;
        try {
            doc = ppMgr.parse(new FileInputStream(file));
        } catch (XMLParserException e) {
            throw new XMLObjectException("Error reading file: " + file.getAbsolutePath(), e);
        } catch (FileNotFoundException e) {
            throw new XMLObjectException("Error reading file: " + file.getAbsolutePath(), e);
        }

        return doc;
    }

    protected XMLObjectHelper() {}

    @SuppressWarnings("unchecked")
    public T build(Element element) {
        T xmlObject = (T) buildXMLObject(element);
        return xmlObject;
    }

    public T buildFromFile(File file) {
        Document doc = readDocumentFromFile(file);
        return build(doc.getDocumentElement());
    }

    public T buildFromFile(String fileName) {
        File file = new File(fileName);
        return buildFromFile(file);
    }
}
