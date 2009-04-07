package org.glite.authz.pap.repository.dao;

import org.glite.authz.pap.repository.dao.filesystem.FileSystemDAOFactory;

/**
 * This class conforms to the Abstract Factory pattern to produce a number of DAOs needed by the
 * application.
 */
public abstract class DAOFactory {

    /**
     * Returns the DAO factory implementation.
     * 
     * @return the DAO factory implementation class.
     */
    public static DAOFactory getDAOFactory() {
        return FileSystemDAOFactory.getInstance();
    }

    /**
     * Returns the PolicySet DAO.
     * 
     * @return the PolicySet DAO implementaion class.
     */
    public abstract PolicySetDAO getPolicySetDAO();

    /**
     * Returns the Policy DAO.
     * 
     * @return the Policy DAO implementaion class.
     */
    public abstract PolicyDAO getPolicyDAO();

    /**
     * Returns the Pap DAO.
     * 
     * @return the Pap DAO implementaion class.
     */
    public abstract PapDAO getPapDAO();
}
