package org.glite.authz.pap.services;

import org.slf4j.Logger;

public class ServiceClassExceptionManager {
	
	public static void logAndThrow(Logger log, RuntimeException e) {
		log.error(e.getMessage());
		
		if (log.isDebugEnabled())
			log.error("Catched exception", e);
		
		throw e;	
	}

}
