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

package test.authz;

import junit.framework.TestCase;

import org.glite.security.util.DN;
import org.glite.security.util.DNHandler;

public class DnTest extends TestCase {

	
	public static final String OPENSSL_ENCODED_DN = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Andrea Ceccanti";
	public static final String RFC2253_ENCODED_DN = "CN=Andrea Ceccanti,L=CNAF,OU=Personal Certificate,O=INFN,C=IT";
	public static final String DN_WITH_SLASHES = "CN=Andrea Ceccanti/user,L=CNAF,OU=Personal Certificate,O=INFN,C=IT";
	
	
	public void testDnParsing(){
		
		DN dn = DNHandler.getDNRFC2253(OPENSSL_ENCODED_DN);
		
		assertEquals(dn, DNHandler.getDNRFC2253(RFC2253_ENCODED_DN));
		assertEquals(dn.getX500(), OPENSSL_ENCODED_DN);
		assertEquals(dn.getRFCDN(), RFC2253_ENCODED_DN);
		
		
		
		
	}
	
	public void testDnWithSlashesParsing(){
		
		DN dn = DNHandler.getDNRFC2253(DN_WITH_SLASHES);
		assertEquals(dn.getRFCDN(), DN_WITH_SLASHES);
	}
	
}
