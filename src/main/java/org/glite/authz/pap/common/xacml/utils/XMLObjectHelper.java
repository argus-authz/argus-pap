package org.glite.authz.pap.common.xacml.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glite.authz.pap.common.exceptions.XMLObjectException;
import org.glite.authz.pap.common.xacml.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.PolicyTypeString;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
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
    private static final Object lock = new Object();

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

    public static void toFile(File file, XMLObject xmlObject) {
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new XMLObjectException("Cannot write to file: " + file.getAbsolutePath(), e);
        }

        write(fos, xmlObject, 4);
    }

    public static void toFile(String fileName, XMLObject xmlObject) {
        File file = new File(fileName);
        toFile(file, xmlObject);
    }

    public static String toString(Element element) {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        write(bos, element);
        return bos.toString();
    }
    
    public static String toString(XMLObject xmlObject) {
        return toString(xmlObject, 4);
    }
    
    public static String toString(XMLObject xmlObject, int indent) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        write(bos, xmlObject, indent);
        return bos.toString();
    }

    public static XMLObject unmarshall(Element element) throws UnmarshallingException {
        synchronized (lock) {
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
            XMLObject xmlObject = unmarshaller.unmarshall(element);
            return xmlObject;
        }
    }
    
    public static XMLObject axisUnmarshall(Element element) throws UnmarshallingException {
        synchronized (lock) {
            XMLObject xmlObject;
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);

            XMLObject xmlObjectDOM = unmarshaller.unmarshall(element);
            
            if (xmlObjectDOM instanceof PolicyType) {
                
                xmlObject = new PolicyTypeString((PolicyType) xmlObjectDOM);
                xmlObject.releaseDOM();
                
            } else if(xmlObjectDOM instanceof PolicySetType) {
                
                xmlObject = new PolicySetTypeString((PolicySetType) xmlObjectDOM);
                xmlObject.releaseDOM();
                
            } else {
                
                xmlObject = xmlObjectDOM;
            }
            return xmlObject;
        }
    }

    public static void write(OutputStream outputStream, Element element) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
//            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.transform(new DOMSource(element), new StreamResult(outputStream));
        } catch (TransformerConfigurationException e) {
            throw new XMLObjectException(e);
        } catch (TransformerException e) {
            throw new XMLObjectException(e);
        }
    }
    
    public static void write(OutputStream outputStream, XMLObject xmlObject, int indent) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
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
            BasicParserPool ppMgr = new BasicParserPool();
            ppMgr.setNamespaceAware(true);
            doc = ppMgr.parse(inputStream);
        } catch (XMLParserException e) {
            throw new XMLObjectException(e);
        }

        return doc;
    }
    
    private static Document readDocument(Reader reader) {
        Document doc;
        try {
            BasicParserPool ppMgr = new BasicParserPool();
            ppMgr.setNamespaceAware(true);
            doc = ppMgr.parse(reader);
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

    public T buildFromString(String s) {
        StringReader sr = new StringReader(s);
        Document doc = readDocument(sr);
        return build(doc.getDocumentElement());
    }

    public T clone(T xmlObject) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        write(bos, xmlObject, 0);
        ByteArrayInputStream ios = new ByteArrayInputStream(bos.toByteArray());
        Document doc = readDocument(ios);
        return build(doc.getDocumentElement());
    }

    public String readFromFileAsString(File file) {
        
        T xmlObject = buildFromFile(file);
        
        return toString(xmlObject);
        
//        char[] buffer;
//        
//        BufferedReader bufferedReader;
//        try {
//            bufferedReader = new BufferedReader(new FileReader(file));
//            buffer = new char[(int) file.length()];
//            bufferedReader.read(buffer);
//        } catch (FileNotFoundException e) {
//            throw new XMLObjectException("File not found: " + file.getAbsolutePath(), e);
//        } catch (IOException e) {
//            throw new XMLObjectException("IO exception reading file: " + file.getAbsolutePath(), e);
//        }
//        
//        return new String(buffer);
    }

    public String readFromFileAsString(String fileName) {
        File file = new File(fileName);
        return readFromFileAsString(file);
    }
}
