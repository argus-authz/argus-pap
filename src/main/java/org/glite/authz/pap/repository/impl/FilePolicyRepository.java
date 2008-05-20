/**************************************************************************

 Copyright 2006-2007 Istituto Nazionale di Fisica Nucleare (INFN)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 File : FilePolicyRepository.java

 Authors: Valerio Venturi <valerio.venturi@cnaf.infn.it>

 **************************************************************************/

package org.glite.authz.pap.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glite.authz.pap.repository.PolicyRepository;
import org.glite.authz.pap.repository.PolicyRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Valerio Venturi <valerio.venturi@cnaf.infn.it>
 *
 */
public class FilePolicyRepository implements PolicyRepository {

  final Logger logger = LoggerFactory.getLogger( FilePolicyRepository.class );

  private Document rootPolicySet;

  public FilePolicyRepository( String policyFileName )
      throws PolicyRepositoryException {

    DocumentBuilder documentBuilder = null;

    try {
      documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch ( ParserConfigurationException e ) {
      throw new PolicyRepositoryException( e );
    }

    File policyFile = new File( policyFileName );

    FileInputStream policyFileInputStream = null;

    try {
      policyFileInputStream = new FileInputStream( policyFile );
    } catch ( FileNotFoundException e ) {
      throw new PolicyRepositoryException( e );
    }

    try {
      rootPolicySet = documentBuilder.parse( policyFileInputStream );
    } catch ( SAXException e ) {
      throw new PolicyRepositoryException( e );
    } catch ( IOException e ) {
      throw new PolicyRepositoryException( e );
    }

  }

  public Element getRootPolicySet() {
    return rootPolicySet.getDocumentElement();
  }

}
