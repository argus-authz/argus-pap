package org.glite.authz.pap.common.xacml;

import java.io.File;
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

import org.glite.authz.pap.common.utils.xacml.FileNotFoundXACMLException;
import org.glite.authz.pap.common.utils.xacml.XACMLException;
import org.w3c.dom.Node;

public abstract class XACMLObject {

	public abstract Node getDOM();

	public void toFile(File file) {

		// Use write()
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"3");
			tr.transform(new DOMSource(getDOM().getOwnerDocument()),
					new StreamResult(fos));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundXACMLException(e);
		} catch (TransformerConfigurationException e) {
			throw new XACMLException(e);
		} catch (TransformerException e) {
			throw new XACMLException(e);
		}
	}

	public void toFile(String fileName) {
		File file = new File(fileName);
		toFile(file);
	}

	public void write(OutputStream outputStream) {
		// TODO
	}
}
