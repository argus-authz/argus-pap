package org.glite.authz.pap.repository.dao.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.glite.authz.pap.repository.PersistenceManager;

public abstract class GenericDAOJpa {

    protected EntityManager entityManager = null;

    public void clear() {
        getEntityManager().clear();
    }

    public void flush() {
        getEntityManager().flush();
    }

    public void setEntityManager(EntityManager em) {
        entityManager = em;
    }

    protected void commitManagedTransaction(EntityTransaction tx) {
        if (tx == null) {
            return;
        }
        tx.commit();
    }

    protected EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = PersistenceManager.getInstance().createEntityManager();
        }
        return entityManager;
    }
    
    protected EntityTransaction manageTransaction() {
        EntityTransaction tx = getEntityManager().getTransaction();
        if (tx.isActive()) {
            return null;
        } else {
            tx.begin();
            return tx;
        }
    }
    
    protected void rollbakManagedTransaction(EntityTransaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
