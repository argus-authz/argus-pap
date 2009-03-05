/**
 * 
 * Copyright [2006-2007] Istituto Nazionale di Fisica Nucleare (INFN)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * File : Serializer.java
 * 
 * Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>
 * 
 */

package org.glite.authz.pap.common.opensamlserializer;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.glite.authz.pap.common.xacml.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.PolicyTypeString;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @author Valerio Venturi (valerio.venturi@cnaf.infn.it)
 * 
 */
public class Serializer implements org.apache.axis.encoding.Serializer {

    private static final long serialVersionUID = -4207218164610553717L;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.axis.encoding.Serializer#serialize(javax.xml.namespace.QName, org.xml.sax.Attributes,
     * java.lang.Object, org.apache.axis.encoding.SerializationContext)
     */
    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException {

        try {

            if (value instanceof PolicyTypeString) {
                
                String policyString = ((PolicyTypeString) value).getPolicyString();
                String ps = policyString.substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length());
                context.setWriteXMLType(null);
                context.writeString(ps);
                
            } else if (value instanceof PolicySetTypeString) {
                
                String policySetString = ((PolicySetTypeString) value).getPolicySetString();
                String pss = policySetString.substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length());
                context.setWriteXMLType(null);
                context.writeString(pss);
                
            } else {

                XMLObject xmlObject = (XMLObject) value;

                Element element = XMLObjectHelper.marshall(xmlObject);

                if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); i++) {
                        element.setAttributeNS(attributes.getURI(i), attributes.getQName(i), attributes.getValue(i));
                    }
                }

                context.setWriteXMLType(null);
                context.writeDOMElement(element);
            }

        } catch (Exception exception) {

            throw new IOException("Error serializing " + value.getClass().getName() + " : " + exception.getClass().getName());

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.axis.encoding.Serializer#writeSchema(java.lang.Class,
     * org.apache.axis.wsdl.fromJava.Types)
     */
    @SuppressWarnings("unchecked")
    public Element writeSchema(Class javaType, Types types) throws Exception {
        Element complexType = types.createElement("complexType");
        return complexType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.rpc.encoding.Serializer#getMechanismType()
     */
    public String getMechanismType() {
        // TODO Auto-generated method stub
        return Constants.AXIS_SAX;
    }

}
