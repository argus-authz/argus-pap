package test.authz;

import org.glite.authz.pap.authz.util.DNImpl;

import junit.framework.TestCase;

public class DnTest extends TestCase {

	
	public static final String OPENSSL_ENCODED_DN = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Andrea Ceccanti";
	public static final String RFC2253_ENCODED_DN = "CN=Andrea Ceccanti,L=CNAF,OU=Personal Certificate,O=INFN,C=IT";
	
	public void testDnHandler(){
		
		assertEquals(new DNImpl(OPENSSL_ENCODED_DN).getRFC2253(), RFC2253_ENCODED_DN);
		assertEquals(new DNImpl(OPENSSL_ENCODED_DN).getX500(), OPENSSL_ENCODED_DN);
		assertEquals(new DNImpl(RFC2253_ENCODED_DN).getX500(), OPENSSL_ENCODED_DN);
		assertEquals(new DNImpl(RFC2253_ENCODED_DN).getRFC2253(), RFC2253_ENCODED_DN);
	}
	
}
