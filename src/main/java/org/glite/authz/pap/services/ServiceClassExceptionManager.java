package org.glite.authz.pap.services;

import org.slf4j.Logger;

public class ServiceClassExceptionManager {
	
	public static void logAndThrow(Logger log, RuntimeException e) {
	
		logException(log, e);
		
		throw e;	
	}
	
	public static void log(Logger log, Throwable e) {
		logException(log, e);
	}
	
	private static void logException(Logger log, Throwable e) {
		
		log.error(e.getMessage());

		if (log.isDebugEnabled()) {
			log.error("Catched exception", e);
		}
	}

}
