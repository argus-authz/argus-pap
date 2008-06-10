package org.glite.authz.pap.repository.dao;

import org.glite.authz.pap.repository.dao.filesystem.FileSystemDAOFactory;

public abstract class DAOFactory {

	public static DAOFactory getDAOFactory() {
		return FileSystemDAOFactory.getInstance();
	}

	public abstract PolicySetDAO getPolicySetDAO();

	public abstract PAPPolicySetDAO getPapDAO();

	public abstract PolicyDAO getPolicyDAO();
	
	public abstract RootPolicySetDAO getRootPolicySetDAO();
	
}