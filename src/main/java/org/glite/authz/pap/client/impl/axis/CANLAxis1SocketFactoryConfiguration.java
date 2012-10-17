package org.glite.authz.pap.client.impl.axis;

public interface CANLAxis1SocketFactoryConfiguration {
	
	/**
	 * @return the sslProtocol
	 */
	public String getSslProtocol();
	/**
	 * @return the trustAnchorsDir
	 */
	public String getTrustAnchorsDir();

	/**
	 * @return the certFile
	 */
	public String getCertFile();

	/**
	 * @return the keyFile
	 */
	public String getKeyFile();

	/**
	 * @return the keyPassword
	 */
	public String getKeyPassword();

	/**
	 * @return the proxyFile
	 */
	public String getProxyFile();

	/**
	 * @return the refreshInterval
	 */
	public long getRefreshInterval();

	/**
	 * @return the timeout
	 */
	public int getTimeout();

	/**
	 * @return the enforcingHostnameChecks
	 */
	public boolean isEnforcingHostnameChecks();
	
	/**
	 * @return the secure random algorithm
	 */
	public String getSecureRandomAlgorithm();
}
