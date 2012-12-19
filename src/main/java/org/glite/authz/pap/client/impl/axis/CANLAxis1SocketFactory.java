package org.glite.authz.pap.client.impl.axis;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.components.net.SecureSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.ValidationError;
import eu.emi.security.authn.x509.ValidationErrorListener;
import eu.emi.security.authn.x509.impl.HostnameMismatchCallback;
import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.impl.SocketFactoryCreator;
import eu.emi.security.authn.x509.impl.X500NameUtils;

/**
 * This class provides a CANL-based replacement for Axis 1.x trustmanager secure
 * socket factory class.
 * 
 * @author andreaceccanti
 *
 */
public class CANLAxis1SocketFactory implements SecureSocketFactory,
		HostnameMismatchCallback, ValidationErrorListener {
	
	public static final Logger log = LoggerFactory.getLogger(CANLAxis1SocketFactory.class.getName());

	private String sslProtocol;

	private String trustAnchorsDir;

	private String certFile;

	private String keyFile;
	private String keyPassword;

	private String proxyFile;
	private long refreshInterval;

	private int timeout;

	private boolean enforcingHostnameChecks;
	
	private String secureRandomAlgorithm;
	
	private static CANLAxis1SocketFactoryConfigurator configurator;

	public CANLAxis1SocketFactory(@SuppressWarnings("rawtypes") final Map attributes) {
		
	}

	public static synchronized void setConfigurator(CANLAxis1SocketFactoryConfigurator conf){
		configurator = conf;
	}
	

	private KeyManager[] getKeymanagers() throws Exception {

		PEMCredential cred;

		if (proxyFile != null){
			cred = new PEMCredential(new FileInputStream(proxyFile), (char[])null);
		}else{

			if (keyPassword != null)
				cred = new PEMCredential(keyFile, certFile, keyPassword.toCharArray());
			else
				cred = new PEMCredential(keyFile, certFile, null);
		}

		return new KeyManager[] { cred.getKeyManager() };

	}

	private TrustManager[] getTrustmanagers() throws Exception {

		OpensslCertChainValidator validator = new OpensslCertChainValidator(
				trustAnchorsDir, NamespaceCheckingMode.EUGRIDPMA_AND_GLOBUS,
				refreshInterval);

		
		X509TrustManager trustManager = SocketFactoryCreator
				.getSSLTrustManager(validator);

		TrustManager[] trustManagers = new TrustManager[] { trustManager };

		return trustManagers;

	}

	private SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
		return SecureRandom.getInstance(secureRandomAlgorithm);
	}

	private SSLSocketFactory createSocketFactory() throws Exception {

		SSLContext context = SSLContext.getInstance(sslProtocol);

		KeyManager[] keyManagers = getKeymanagers();
		TrustManager[] trustManagers = getTrustmanagers();
		SecureRandom random = getSecureRandom();

		context.init(keyManagers, trustManagers, random);

		return context.getSocketFactory();

	}

	public Socket create(String host, int port, StringBuffer otherHeaders,
			BooleanHolder useFullURL) throws Exception {

		configurator.configure(this);
		
		SocketFactory fac = createSocketFactory();

		SSLSocket socket = (SSLSocket) fac.createSocket(host,port);
		socket.setEnabledProtocols(new String[] { sslProtocol });
		socket.setSoTimeout(timeout);
		SocketFactoryCreator.connectWithHostnameChecking(socket, this);
		return socket;
	}

	public void nameMismatch(SSLSocket socket, X509Certificate peerCertificate,
			String hostName) throws SSLException {
		
		if (enforcingHostnameChecks) {
			try {

				socket.close();

			} catch (IOException e) {

			}

			String peerCertificateSubject = X500NameUtils
					.getReadableForm(peerCertificate.getSubjectX500Principal());
			String message = String.format(
					"Peer certificate subject %s does not match hostname %s",
					peerCertificateSubject, hostName);
			throw new SSLException(message);

		}
	}

	public boolean onValidationError(ValidationError error) {
		log.error("Certificate validation error: {}", error);
		return false;
	}

	/**
	 * @param sslProtocol the sslProtocol to set
	 */
	public synchronized void setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
	}

	/**
	 * @param trustAnchorsDir the trustAnchorsDir to set
	 */
	public synchronized void setTrustAnchorsDir(String trustAnchorsDir) {
		this.trustAnchorsDir = trustAnchorsDir;
	}

	/**
	 * @param certFile the certFile to set
	 */
	public synchronized void setCertFile(String certFile) {
		this.certFile = certFile;
	}

	/**
	 * @param keyFile the keyFile to set
	 */
	public synchronized void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}

	/**
	 * @param keyPassword the keyPassword to set
	 */
	public synchronized void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	/**
	 * @param proxyFile the proxyFile to set
	 */
	public synchronized void setProxyFile(String proxyFile) {
		this.proxyFile = proxyFile;
	}

	/**
	 * @param refreshInterval the refreshInterval to set
	 */
	public synchronized void setRefreshInterval(long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public synchronized void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @param enforcingHostnameChecks the enforcingHostnameChecks to set
	 */
	public synchronized void setEnforcingHostnameChecks(
			boolean enforcingHostnameChecks) {
		this.enforcingHostnameChecks = enforcingHostnameChecks;
	}

	/**
	 * @param secureRandomAlgorithm the secureRandomAlgorithm to set
	 */
	public synchronized void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
		this.secureRandomAlgorithm = secureRandomAlgorithm;
	}	
	
}
