/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.glite.authz.pap.common.exceptions.XMLObjectMarshallingException;
import org.glite.authz.pap.common.exceptions.XMLObjectParserException;
import org.glite.authz.pap.common.exceptions.XMLObjectUnmarshallingException;
import org.glite.authz.pap.common.xacml.impl.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.impl.PolicyTypeString;
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

/**
 * The <code>XMLObjectHelper</code> class contains helper methods to deal with OpenSAML
 * <code>XMLObject</code> objects.
 * 
 * @param <T> a class extending <code>org.opensaml.xml.XMLObject</code>
 */
public class XMLObjectHelper<T extends XMLObject> {

    protected static final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
    private static final Object lock = new Object();

    protected XMLObjectHelper() {}

    /**
     * Unmarshalls an <code>Element</code> into an <code>XMLObject</code>. Thread safe method.
     * <p>
     * This unmarshalling method is used inside the Axis deserialization process. If
     * <code>Element</code> is unmarshalled into a <code>PolicyType</code> or
     * <code>PolicySetType</code> object then, in order to cut on memory usage, the implementing
     * classes to represent the given element are, respectively, <code>PolicyTypeString</code> or
     * <code>PolicySetTypeString</code>.
     * 
     * @param element
     * @return
     * @throws UnmarshallingException
     */
    public static XMLObject axisUnmarshall(Element element) throws UnmarshallingException {
        synchronized (lock) {
            XMLObject xmlObject;
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);

            XMLObject xmlObjectDOM = unmarshaller.unmarshall(element);

            if (xmlObjectDOM instanceof PolicyType) {

                xmlObject = new PolicyTypeString((PolicyType) xmlObjectDOM);
                xmlObject.releaseDOM();

            } else if (xmlObjectDOM instanceof PolicySetType) {

                xmlObject = new PolicySetTypeString((PolicySetType) xmlObjectDOM);
                xmlObject.releaseDOM();

            } else {

                xmlObject = xmlObjectDOM;
            }
            return xmlObject;
        }
    }

    /**
     * Build an <code>XMLObject</code> from an <code>Element</code>.
     * 
     * @param element an element representing an OpenSAML <code>XMLObject</code>.
     * @return the given element as an OpenSAML <code>XMLObject</code>.
     * 
     * @throws XMLObjectUnmarshallingException if unmarshalling fails.
     */
    public static XMLObject buildXMLObject(Element element) {
        XMLObject xmlObject;

        try {
            xmlObject = unmarshall(element);
        } catch (UnmarshallingException e) {
            throw new XMLObjectUnmarshallingException(e);
        }
        return xmlObject;
    }

    /**
     * Build an <code>XMLObject</code> from an <code>InputStream</code>.
     * 
     * @param inputStream input stream representing an OpenSAML <code>XMLObject</code>.
     * @return the given input stream as an OpenSAML <code>XMLObject</code>.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     */
    public static XMLObject buildXMLObject(InputStream inputStream) {
        Document doc = readDocument(inputStream);
        return buildXMLObject(doc.getDocumentElement());
    }

    /**
     * Build an <code>XMLObject</code> from file.
     * 
     * @param file a file containing an OpenSAML <code>XMLObject</code>.
     * @return the OpenSAML <code>XMLObject</code>.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     * @throws XMLObjectException wrapping a {@link FileNotFoundException} if file does not exist.
     */
    public static XMLObject buildXMLObjectFromFile(File file) {
        Document doc = readDocument(file);
        return buildXMLObject(doc.getDocumentElement());
    }

    /**
     * Build an <code>XMLObject</code> from file.
     * 
     * @param fileName <code>String</code> representing the file name containing an OpenSAML
     *            <code>XMLObject</code>.
     * @return the OpenSAML <code>XMLObject</code>.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     * @throws XMLObjectException wrapping a {@link FileNotFoundException} if file does not exist.
     */
    public static XMLObject buildXMLObjectFromFile(String fileName) {
        return buildXMLObjectFromFile(new File(fileName));
    }

    /**
     * Get the DOM of an <code>XMLObject</code>.
     * 
     * @param xmlObject
     * @return the DOM of the given object.
     * 
     * @throws XMLObjectMarshallingException if the marshalling fails.
     */
    public static Element getDOM(XMLObject xmlObject) {
        Element element;

        try {
            element = marshall(xmlObject);
        } catch (MarshallingException e) {
            throw new XMLObjectMarshallingException(e);
        }

        return element;
    }

    /**
     * Marshalls an <code>XMLObject</code>. Thread safe method.
     * 
     * @param xmlObject
     * @return the marshalled <code>XMLObject</code>.
     * 
     * @throws MarshallingException
     */
    public static Element marshall(XMLObject xmlObject) throws MarshallingException {
        Element element;
        synchronized (lock) {
            MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
            Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
            element = marshaller.marshall(xmlObject);
        }
        return element;
    }

    /**
     * Write an <code>XMLObject</code> to a file.
     * 
     * @param file
     * @param xmlObject
     * 
     * @throws XMLObjectException wrapping a {@link FileNotFoundException}, a
     *             {@link TransformerConfigurationException} or a {@link TransformerException}.
     */
    public static void toFile(File file, XMLObject xmlObject) {
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new XMLObjectException("Cannot write to file: " + file.getAbsolutePath(), e);
        }

        write(fos, xmlObject, 4);
    }

    /**
     * Write an <code>XMLObject</code> to a file.
     * 
     * @param fileName the file name as <code>String</code>.
     * @param xmlObject
     * 
     * @throws XMLObjectException wrapping a {@link FileNotFoundException}, a
     *             {@link TransformerConfigurationException} or a {@link TransformerException}.
     */
    public static void toFile(String fileName, XMLObject xmlObject) {
        File file = new File(fileName);
        toFile(file, xmlObject);
    }

    /**
     * Returns the <code>String</code> representation of the given <code>Element<code>.
     * 
     * @param element
     * @return <code>String</code> representation of the element.
     * 
     * @throws XMLObjectException wrapping a {@link TransformerConfigurationException} or a
     *             {@link TransformerException}.
     */
    public static String toString(Element element) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        write(bos, element);
        return bos.toString();
    }

    /**
     * Returns the <code>String</code> representation of the given <code>XMLObject<code>.
     * 
     * @param element
     * @return <code>String</code> representation of the element.
     * 
     * @throws XMLObjectException wrapping a {@link TransformerConfigurationException} or a
     *             {@link TransformerException}.
     */
    public static String toString(XMLObject xmlObject) {
        return toString(xmlObject, 4);
    }

    /**
     * Returns the <code>String</code> representation of the given
     * <code>XMLObject<code> allowing to specify an indent value.
     * 
     * @param element
     * @param indent indent value.
     * @return <code>String</code> representation of the element.
     * 
     * @throws XMLObjectException wrapping a {@link TransformerConfigurationException} or a
     *             {@link TransformerException}.
     */
    public static String toString(XMLObject xmlObject, int indent) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        write(bos, xmlObject, indent);
        return bos.toString();
    }

    /**
     * Unmarshalls an <code>Element</code> into an <code>XMLObject</code>. Thread safe method.
     * 
     * @param element the <code>Element</code> to be marshalled.
     * @return the corresponding <code>XMLObject</code>.
     * 
     * @throws UnmarshallingException
     */
    public static XMLObject unmarshall(Element element) throws UnmarshallingException {
        synchronized (lock) {
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
            XMLObject xmlObject = unmarshaller.unmarshall(element);
            return xmlObject;
        }
    }

    /**
     * Writes an <code>Element</code> into an <code>OutputStream</code>.
     * 
     * @param outputStream
     * @param element
     * 
     * @throws XMLObjectException wrapping a {@link TransformerConfigurationException} or a
     *             {@link TransformerException}.
     */
    public static void write(OutputStream outputStream, Element element) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.transform(new DOMSource(element), new StreamResult(outputStream));
        } catch (TransformerConfigurationException e) {
            throw new XMLObjectException(e);
        } catch (TransformerException e) {
            throw new XMLObjectException(e);
        }
    }

    /**
     * Writes an <code>XMLObject</code> into an <code>OutputStream</code>.
     * 
     * @param outputStream
     * @param xmlObject
     * @param indent indentation value.
     * 
     * @throws XMLObjectException wrapping a {@link TransformerConfigurationException} or a
     *             {@link TransformerException}.
     */
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

    /**
     * Get a <code>Document</code> from a <code>File</code>.
     * 
     * @param file the file to read the document from.
     * @return the file content as <code>Document</code>.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     * @throws XMLObjectException wrapping a {@link FileNotFoundException} if file does not exist.
     */
    private static Document readDocument(File file) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new XMLObjectException("File not found: " + file.getAbsolutePath(), e);
        }

        return readDocument(fileInputStream);
    }

    /**
     * Get a <code>Document</code> from an <code>InputStream</code>.
     * 
     * @param inputStream
     * @return the given input stream as <code>Document</code>.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     */
    private static Document readDocument(InputStream inputStream) {
        Document doc;
        try {
            BasicParserPool ppMgr = new BasicParserPool();
            ppMgr.setNamespaceAware(true);
            doc = ppMgr.parse(inputStream);
        } catch (XMLParserException e) {
            throw new XMLObjectParserException(e);
        }

        return doc;
    }

    /**
     * Get a <code>Document</code> from a <code>Reader</code>.
     * 
     * @param reader
     * @return the given reader stream as <code>Document</code>.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     */
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

    /**
     * Builds an object of type <code>T</code> from an <code>Element</code>.
     * 
     * @param element an element representing an OpenSAML <code>XMLObject</code>.
     * @return the OpenSAML object of class <code>T</code> extending <code>XMLObject</code>.
     * 
     * @throws XMLObjectUnmarshallingException if unmarshalling fails.
     */
    @SuppressWarnings("unchecked")
    public T build(Element element) {
        T xmlObject = (T) buildXMLObject(element);
        return xmlObject;
    }

    /**
     * Build an object of type <code>T</code> from file.
     * 
     * @param file
     * @return the object <code>T</code> representing the content of the file.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     * @throws XMLObjectException wrapping a {@link FileNotFoundException} if file does not exist.
     */
    public T buildFromFile(File file) {
        Document doc = readDocument(file);
        return build(doc.getDocumentElement());
    }

    /**
     * Build an object of type <code>T</code> from a <code>String</code>.
     * 
     * @param s the <code>String</code> to be parsered into an object <code>T</code>.
     * @return the object <code>T</code> representing the content of the string.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     */
    public T buildFromString(String s) {
        StringReader sr = new StringReader(s);
        Document doc = readDocument(sr);
        return build(doc.getDocumentElement());
    }

    /**
     * Clone an <code>XMLObject</code>.
     * 
     * @param xmlObject <code>XMLObject</code> to be cloned.
     * @return a clone of the given object.
     * 
     * @throws XMLObjectException wrapping a {@link TransformerConfigurationException} or a
     *             {@link TransformerException} .
     * @throws XMLObjectParserException if the XML parsing fails.
     */
    public T clone(T xmlObject) {
        // TODO: just marshalling and unmarshalling doesn't work, it gets a null
        // pointer exception during unmarshalling... find out what's wrong
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        write(bos, xmlObject, 0);
        ByteArrayInputStream ios = new ByteArrayInputStream(bos.toByteArray());
        Document doc = readDocument(ios);
        return build(doc.getDocumentElement());
    }

    /**
     * Reads an <code>XMLObject</code> from file and returns the <code>String</code> representation.
     * 
     * @param file the file to read.
     * @return <code>String</code> representation of the <code>XMLObject</code> contained in the
     *         file.
     * 
     * @throws XMLObjectParserException if the XML parsing fails.
     * @throws XMLObjectException wrapping a {@link FileNotFoundException} if file does not exist.
     * @throws XMLObjectException wrapping a {@link TransformerConfigurationException} or a
     *             {@link TransformerException} .
     */
    public String readFromFileAsString(File file) {

        T xmlObject = buildFromFile(file);

        return toString(xmlObject);
    }
}
