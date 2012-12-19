package org.glite.authz.pap.authz;

import java.io.File;
import java.security.cert.X509Certificate;

import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStoreStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.FormatMode;

public class VOMSListener implements VOMSTrustStoreStatusListener, ValidationResultListener {

	public static final Logger logger = LoggerFactory.getLogger(VOMSListener.class);
	
	public void notifyCertficateLookupEvent(String dir) {
		logger.debug("Looking for VOMS trusted certificates in {}", dir);
	}

	public void notifyLSCLookupEvent(String dir) {
		logger.debug("Looking for VOMS LSC files in {}", dir);
	}

	public void notifyCertificateLoadEvent(X509Certificate cert, File f) {
		logger.debug("VOMS AA certificate loaded from {}", f.getAbsolutePath());
		logger.debug("Certificate info: {}", CertificateUtils.format(cert, FormatMode.MEDIUM_ONE_LINE));
	}

	public void notifyLSCLoadEvent(LSCInfo lsc, File f) {
		logger.debug("VOMS LSC file loaded from {}", f.getAbsolutePath());
		logger.debug("LSC info: {}", lsc);
	}

	public void notifyValidationResult(VOMSValidationResult result) {
		if (!result.isValid()) {
			logger.error("VOMS AC validation for VO {} failed for the following reasons:", result.getAttributes().getVO());
			for (VOMSValidationErrorMessage m : result.getValidationErrors())
				logger.error("{}", m.getMessage());
		} else {
			logger.debug("VOMS AC validation for VO %s succeded.\n", result.getAttributes().getVO());
		}
	}
}
