package test.authz;

import java.io.StringReader;

import org.glite.authz.pap.authz.ACL;
import org.glite.authz.pap.authz.AuthzConfigurationParser;

import junit.framework.TestCase;

public class AuthzConfigurationParserTest extends TestCase {

	public static final String EMPTY_CONF_FILE="[dn]\n\n[fqan]\n\n";
	
	public void testEmptyFileParseSuccess(){
		
		AuthzConfigurationParser parser = AuthzConfigurationParser.instance();
		ACL acl = parser.parse(new StringReader(EMPTY_CONF_FILE));
		assertTrue(acl.getPermissions().isEmpty());
		
	}
}
