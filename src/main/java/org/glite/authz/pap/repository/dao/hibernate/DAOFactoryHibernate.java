package org.glite.authz.pap.repository.dao.hibernate;

import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PapDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;

public class DAOFactoryHibernate extends DAOFactory {
    private static DAOFactoryHibernate instance = null;

    public static DAOFactoryHibernate getInstance() {
        if (instance == null) {
            instance = new DAOFactoryHibernate();
        }
        return instance;
    }

    private DAOFactoryHibernate() {}

    @Override
    public PolicyDAO getPolicyDAO() {
        return PolicyDAOHibernate.getInstance();
    }

    @Override
    public PolicySetDAO getPolicySetDAO() {
        return PolicySetDAOHibernate.getInstance();
    }

    @Override
    public PapDAO getPapDAO() {
        return PapDAOHibernate.getInstance();
    }

}
