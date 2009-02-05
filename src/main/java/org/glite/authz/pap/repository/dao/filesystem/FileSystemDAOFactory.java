package org.glite.authz.pap.repository.dao.filesystem;

import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PAPDAO;
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

    private FileSystemDAOFactory() {}

    @Override
    public PolicyDAO getPolicyDAO() {
        return FileSystemPolicyDAO.getInstance();
    }

    @Override
    public PolicySetDAO getPolicySetDAO() {
        return FileSystemPolicySetDAO.getInstance();
    }

    @Override
    public PAPDAO getPAPDAO() {
        return FileSystemPAPDAO.getInstance();
    }
    
}
