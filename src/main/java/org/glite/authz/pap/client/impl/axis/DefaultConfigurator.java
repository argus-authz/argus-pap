package org.glite.authz.pap.client.impl.axis;

import org.italiangrid.utils.https.impl.canl.CANLListener;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;

public class DefaultConfigurator implements CANLAxis1SocketFactoryConfigurator, 
	CANLAxis1SocketFactoryConfiguration {

	public static final String DEFAULT_TRUST_ANCHORS_DIR = "/etc/grid-security/certificates";
	public static final String DEFAULT_PROTOCOL = "SSLv3";
	public static final String DEFAULT_SECURE_RANDOM = "SHA1PRNG";
	
	public static final String DEFAULT_SSL_CERT_FILE = "/etc/grid-security/hostcert.pem";
    public static final String DEFAULT_SSL_KEY = "/etc/grid-security/hostkey.pem";
    
	public static final boolean DEFAULT_CHECK_HOSTNAME_POLICY = false;
	
	public static final long DEFAULT_REFRESH_INTERVAL = 60000L;
	public static final int DEFAULT_TIMEOUT = 60000;
	
	private String sslProtocol = DEFAULT_PROTOCOL;

	private String trustAnchorsDir = DEFAULT_TRUST_ANCHORS_DIR;

	private String certFile = DEFAULT_SSL_CERT_FILE;

	private String keyFile = DEFAULT_SSL_KEY;
	
	private String keyPassword = null;

	private String proxyFile = null;
	
	private long refreshInterval = DEFAULT_REFRESH_INTERVAL;

	private int timeout = DEFAULT_TIMEOUT;

	private boolean enforcingHostnameChecks = DEFAULT_CHECK_HOSTNAME_POLICY;
	
	private String secureRandomAlgorithm = DEFAULT_SECURE_RANDOM;
	
	private static volatile X509CertChainValidatorExt validator = null;
	
	public DefaultConfigurator() {
		
	}

	private synchronized X509CertChainValidatorExt getValidator(){
		
		if (validator == null){
			
			CANLListener l = new CANLListener();
			validator = CertificateValidatorBuilder.buildCertificateValidator(trustAnchorsDir,
				l,
				l,
				refreshInterval);
		}
		
		return validator;
	}
	
	public synchronized void configure(CANLAxis1SocketFactory factory) {
		factory.setSecureRandomAlgorithm(getSecureRandomAlgorithm());
		factory.setSslProtocol(getSslProtocol());
		factory.setCertChainValidator(getValidator());
		factory.setCertFile(getCertFile());
		factory.setKeyFile(getKeyFile());
		factory.setKeyPassword(getKeyPassword());
		
		factory.setProxyFile(getProxyFile());
		
		factory.setTimeout(getTimeout());
		factory.setEnforcingHostnameChecks(isEnforcingHostnameChecks());
	}

	/**
	 * @return the sslProtocol
	 */
	public synchronized String getSslProtocol() {
		return sslProtocol;
	}

	/**
	 * @param sslProtocol the sslProtocol to set
	 */
	public synchronized void setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
	}

	/**
	 * @return the trustAnchorsDir
	 */
	public synchronized String getTrustAnchorsDir() {
		return trustAnchorsDir;
	}

	/**
	 * @param trustAnchorsDir the trustAnchorsDir to set
	 */
	public synchronized void setTrustAnchorsDir(String trustAnchorsDir) {
		this.trustAnchorsDir = trustAnchorsDir;
	}

	/**
	 * @return the certFile
	 */
	public synchronized String getCertFile() {
		return certFile;
	}

	/**
	 * @param certFile the certFile to set
	 */
	public synchronized void setCertFile(String certFile) {
		this.certFile = certFile;
	}

	/**
	 * @return the keyFile
	 */
	public synchronized String getKeyFile() {
		return keyFile;
	}

	/**
	 * @param keyFile the keyFile to set
	 */
	public synchronized void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}

	/**
	 * @return the keyPassword
	 */
	public synchronized String getKeyPassword() {
		return keyPassword;
	}

	/**
	 * @param keyPassword the keyPassword to set
	 */
	public synchronized void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	/**
	 * @return the proxyFile
	 */
	public synchronized String getProxyFile() {
		return proxyFile;
	}

	/**
	 * @param proxyFile the proxyFile to set
	 */
	public synchronized void setProxyFile(String proxyFile) {
		this.proxyFile = proxyFile;
	}

	/**
	 * @return the refreshInterval
	 */
	public synchronized long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @param refreshInterval the refreshInterval to set
	 */
	public synchronized void setRefreshInterval(long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	/**
	 * @return the timeout
	 */
	public synchronized int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public synchronized void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the enforcingHostnameChecks
	 */
	public synchronized boolean isEnforcingHostnameChecks() {
		return enforcingHostnameChecks;
	}

	/**
	 * @param enforcingHostnameChecks the enforcingHostnameChecks to set
	 */
	public synchronized void setEnforcingHostnameChecks(
			boolean enforcingHostnameChecks) {
		this.enforcingHostnameChecks = enforcingHostnameChecks;
	}

	/**
	 * @return the secureRandomAlgorithm
	 */
	public synchronized String getSecureRandomAlgorithm() {
		return secureRandomAlgorithm;
	}

	/**
	 * @param secureRandomAlgorithm the secureRandomAlgorithm to set
	 */
	public synchronized void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
		this.secureRandomAlgorithm = secureRandomAlgorithm;
	}

}
