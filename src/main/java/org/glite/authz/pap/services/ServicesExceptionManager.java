package org.glite.authz.pap.services;

import org.glite.authz.pap.repository.PersistenceManager;
import org.hibernate.Transaction;
import org.slf4j.Logger;

public class ServicesExceptionManager {
	
	public static void logAndThrow(Logger log, RuntimeException e) {
	
		log(log, e);
		throw e;	
	}
	
	public static void log(Logger log, Throwable e) {
	    log.error(e.getMessage());

        if (log.isDebugEnabled()) {
            log.error("Catched exception", e);
        }
	}
	
	public static void logAndRollback(Logger log, Throwable e) {
	    
	    Transaction tx = PersistenceManager.getInstance().getCurrentSession().getTransaction();
	    
	    if (tx != null) {
	        if (tx.isActive()) {
	            try {
	            tx.rollback();
	            } catch (RuntimeException rollbackEx) {
	                log.error("Couldn't roll back transaction.", rollbackEx);
	            }
	        }
	    }
	    log(log, e);
	}
}
