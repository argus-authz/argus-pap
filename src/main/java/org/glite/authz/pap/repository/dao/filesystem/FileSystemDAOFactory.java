package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;

import org.glite.authz.pap.common.RepositoryConfiguration;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemDAOFactory extends DAOFactory {
	private static FileSystemDAOFactory instance = null;

	public static FileSystemDAOFactory getInstance() {
		if (instance == null) {
			instance = new FileSystemDAOFactory();
		}
		return instance;
	}

	private FileSystemDAOFactory() {
		initDB();
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

	// Maybe not the right place for initializing the DB
	private void initDB() {
		File rootDir = new File(RepositoryConfiguration.getFileSystemDatabaseDir());
		if (!rootDir.exists()) {
			if (!rootDir.mkdirs()) {
				throw new RepositoryException("Cannot create DB dir");
			}
		}
		if (!(rootDir.canRead() && rootDir.canWrite())) {
			throw new RepositoryException("Permission denied for DB dir");
		}
	}
}
