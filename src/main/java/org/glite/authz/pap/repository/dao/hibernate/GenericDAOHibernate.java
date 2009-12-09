package org.glite.authz.pap.repository.dao.hibernate;

import org.glite.authz.pap.repository.PersistenceManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class GenericDAOHibernate {

    protected Session session = null;

    public void clear() {
        getSession().clear();
    }

    public void flush() {
        getSession().flush();
    }

    public void setEntityManager(Session session) {
        this.session = session;
    }

    protected void commitManagedTransaction(Transaction tx) {
        if (tx == null) {
            return;
        }
        tx.commit();
    }

    protected Session getSession() {
        if (session == null) {
            session = PersistenceManager.getInstance().getCurrentSession();
        }
        return session;
    }
    
    protected Transaction manageTransaction() {
        Transaction tx = getSession().getTransaction();
        if (tx.isActive()) {
            return null;
        } else {
            tx.begin();
            return tx;
        }
    }
    
    protected void rollbakManagedTransaction(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
