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
 * File : DeserializerFactory.java
 * 
 * Authors: Alberto Forti (alberto.forti@cnaf.infn.it)
 * 
 */

package org.glite.authz.pap.common.opensamlserializer;

import java.util.Iterator;

import javax.xml.rpc.encoding.Deserializer;

public class PolicyTypeDeserializerFactory implements org.apache.axis.encoding.DeserializerFactory {
    
    private static final long serialVersionUID = 7198603279842211488L;

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.rpc.encoding.DeserializerFactory#getDeserializerAs(java.lang.String)
     */
    public Deserializer getDeserializerAs(String arg0) {
        return new org.glite.authz.pap.common.opensamlserializer.PolicyTypeDeserializer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.rpc.encoding.DeserializerFactory#getSupportedMechanismTypes()
     */
    @SuppressWarnings("unchecked")
    public Iterator getSupportedMechanismTypes() {
        // TODO Auto-generated method stub
        return null;
    }

}
