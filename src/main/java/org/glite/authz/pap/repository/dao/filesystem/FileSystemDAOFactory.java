package org.glite.authz.pap.repository.dao.filesystem;

import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;

public class FileSystemDAOFactory extends DAOFactory {
    private static FileSystemDAOFactory instance = null;

    public static FileSystemDAOFactory getInstance() {
	if (instance == null) {
	    instance = new FileSystemDAOFactory();
	}
	return instance;
    }

    private FileSystemDAOFactory() {
    }

    public PolicyDAO getPolicyDAO() {
	return FileSystemPolicyDAO.getInstance();
    }

    public PolicySetDAO getPolicySetDAO() {
	return FileSystemPolicySetDAO.getInstance();
    }
}
