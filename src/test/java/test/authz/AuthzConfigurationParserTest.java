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

import java.io.StringReader;

import org.glite.authz.pap.authz.ACL;
import org.glite.authz.pap.authz.AuthzConfigurationParser;
import org.glite.authz.pap.authz.PAPAdmin;
import org.glite.authz.pap.authz.PAPAdminFactory;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.exceptions.PAPAuthzConfigurationException;

import junit.framework.TestCase;

public class AuthzConfigurationParserTest extends TestCase {

	public static final String EMPTY_CONF_FILE="[dn]\n\n[fqan]\n\n";
	
	public static final String DN_STANZA = "[dn]\n";
	
	public static final String FQAN_STANZA = "[fqan]\n";
	
	public static final String OPENSSL_ENCODED_DN = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Andrea Ceccanti";
	
	public static final String RFC2253_ENCODED_DN = "CN=Andrea Ceccanti,L=CNAF,OU=Personal Certificate,O=INFN,C=IT";
	
	public static final String KERBERIZED_STYLE_DN = "/C=DE/O=GermanGrid/OU=RWTH/CN=host/grid-mon.physik.rwth-aachen.de";
	
	public static final String ALL_PERMISSIONS = "ALL";
	
	public static final String LEGAL_FQAN = "/atlas";
	
	public static final String LEGAL_FQAN_WITH_ROLE = "/atlas/Role=lcgadmin";
	
	public static final String LEGAL_FQAN_LONG_FORMAT = "/dteam/Role=NULL/Capability=NULL";
	
	public static final String LEGAL_FQAN_WITH_ROLE_LONG_FORMAT = "/atlas/Role=lcgadmin/Capability=NULL";
	
	public static final String ILLEGAL_FQAN = "/%asddas/%Role=dsadad/Capabilweru=cic";
	
	
	
	
	private String buildDNPermLine(String principal, String permission){
		
		return String.format("\"%s\" : %s", principal, permission);
		
	}
	
	
	private String buildFQANPermLine(String fqan, String permission){
		return String.format("%s : %s", fqan, permission);
	}
	
	
	protected void assertEqualPermissions(String message, PAPPermission p, PAPPermission p2){
		assertTrue(message, p.satisfies(p2) && p2.satisfies(p2));
	}
	
	private void checkDnPermissions(String principal, String permString, ACL acl){
		
		PAPAdmin  admin = PAPAdminFactory.getDn(principal);
		PAPPermission perms = PAPPermission.fromString(permString);
		
		checkPermissions(admin, perms, acl);
		
	}
	
	
	private void checkFQANPermissions(String fqan, String permString, ACL acl){
		
		PAPAdmin admin = PAPAdminFactory.getFQAN(fqan);
		PAPPermission perms = PAPPermission.fromString(permString);
		
		checkPermissions(admin, perms, acl);
	}
	
	
	private void checkPermissions(PAPAdmin admin, PAPPermission perms, ACL acl){
	
		assertNotNull("Permissions not found for admin '"+admin+"'.",acl.getPermissions().get(admin));
		PAPPermission parsedPerms = acl.getPermissions().get(admin);
		assertEqualPermissions("Parsed permissions do not match!", perms, parsedPerms);
		
	}
	
	
	public void testEmptyFileParseSuccess(){
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(EMPTY_CONF_FILE));
		assertTrue("The parsed ACL should be empty!",acl.getPermissions().isEmpty());
		
	}
	
	
	
	public void testOpensslFormatDN(){
		
		
		StringBuilder confFile = new StringBuilder();
		confFile.append(DN_STANZA);
		confFile.append(buildDNPermLine(OPENSSL_ENCODED_DN, ALL_PERMISSIONS));
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(confFile.toString()));
		
		assertFalse("The parsed ACL should contain one entry!", acl.getPermissions().isEmpty());
		checkDnPermissions(OPENSSL_ENCODED_DN, ALL_PERMISSIONS, acl);
		
	}
	
	public void testRFC2253FormatDN(){
		
		StringBuilder confFile = new StringBuilder();
		confFile.append(DN_STANZA);
		confFile.append(buildDNPermLine(RFC2253_ENCODED_DN, ALL_PERMISSIONS));
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(confFile.toString()));
		
		assertFalse("The parsed ACL should contain one entry!", acl.getPermissions().isEmpty());
		checkDnPermissions(RFC2253_ENCODED_DN, ALL_PERMISSIONS, acl);
	
	}
	
	public void testKeberizedDNParsing(){
	    StringBuilder confFile = new StringBuilder();
	    confFile.append(DN_STANZA);
	    
	    confFile.append(buildDNPermLine(KERBERIZED_STYLE_DN, ALL_PERMISSIONS));
	    
	    AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
	    ACL acl = parser.parse(new StringReader(confFile.toString()));
		
	    assertFalse("The parsed ACL should contain one entry!", acl.getPermissions().isEmpty());
	    checkDnPermissions(KERBERIZED_STYLE_DN, ALL_PERMISSIONS, acl);
	    
	    
	}
	public void testFQANParsing(){
		
		StringBuilder confFile = new StringBuilder();
		confFile.append(FQAN_STANZA);
		confFile.append(buildFQANPermLine(LEGAL_FQAN, ALL_PERMISSIONS));
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(confFile.toString()));
		
		assertFalse("The parsed ACL should contain one entry!", acl.getPermissions().isEmpty());
		
		checkFQANPermissions(LEGAL_FQAN, ALL_PERMISSIONS, acl);
		
	}
	
	public void testFQANLongFormatParsing(){
		
		StringBuilder confFile = new StringBuilder();
		confFile.append(FQAN_STANZA);
		confFile.append(buildFQANPermLine(LEGAL_FQAN_LONG_FORMAT, ALL_PERMISSIONS));
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(confFile.toString()));
		
		assertFalse("The parsed ACL should contain one entry!", acl.getPermissions().isEmpty());
		
		checkFQANPermissions(LEGAL_FQAN_LONG_FORMAT, ALL_PERMISSIONS, acl);
		
	}
	
	public void testRoleFQANParsing(){
		
		StringBuilder confFile = new StringBuilder();
		confFile.append(FQAN_STANZA);
		confFile.append(buildFQANPermLine(LEGAL_FQAN_WITH_ROLE, ALL_PERMISSIONS));
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(confFile.toString()));
		
		assertFalse("The parsed ACL should contain one entry!", acl.getPermissions().isEmpty());
		
		checkFQANPermissions(LEGAL_FQAN_WITH_ROLE, ALL_PERMISSIONS, acl);
		
	}
	
	public void testRoleFQANLongFormatParsing(){
		
		StringBuilder confFile = new StringBuilder();
		confFile.append(FQAN_STANZA);
		confFile.append(buildFQANPermLine(LEGAL_FQAN_WITH_ROLE_LONG_FORMAT, ALL_PERMISSIONS));
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(confFile.toString()));
		
		assertFalse("The parsed ACL should contain one entry!", acl.getPermissions().isEmpty());
		
		checkFQANPermissions(LEGAL_FQAN_WITH_ROLE_LONG_FORMAT, ALL_PERMISSIONS, acl);
		
	}
	
	public void testIllegalFQANParsing(){
		
		StringBuilder confFile = new StringBuilder();
		confFile.append(FQAN_STANZA);
		confFile.append(buildFQANPermLine(ILLEGAL_FQAN, ALL_PERMISSIONS));
		
		boolean caughtException = false;
		
		try{
			AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
			ACL acl = parser.parse(new StringReader(confFile.toString()));
		
		}catch (PAPAuthzConfigurationException e) {
			
			caughtException = true;
		}
		
		assertTrue("Configuration parser succeded in parsing an illegal fqan!",caughtException);
		
		
	}
	
}
