package org.glite.authz.pap.repository.dao.filesystem;

import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;

public class FileSystemDAOFactory extends DAOFactory {
	private static FileSystemDAOFactory instance = null;

	public static FileSystemDAOFactory getInstance() {
		if (instance == null) {
			instance = new FileSystemDAOFactory();
		}
		return instance;
	}

	private FileSystemDAOFactory() { }

	public PAPPolicySetDAO getPapDAO() {
		return FileSystemPapDAO.getInstance();
	}

	public PolicyDAO getPolicyDAO() {
		return FileSystemPolicyDAO.getInstance();
	}

	public PolicySetDAO getPolicySetDAO() {
		return FileSystemPolicySetDAO.getInstance();
	}

	public RootPolicySetDAO getRootPolicySetDAO() {
		return FileSystemRootPolicySetDAO.getInstance();
	}
}
