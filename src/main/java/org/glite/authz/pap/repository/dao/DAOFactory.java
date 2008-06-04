package org.glite.authz.pap.repository.dao;

public abstract class DAOFactory {

	public static DAOFactory getDAOFactory() {
		return FileSystemDAOFactory.getInstance();
	}

	public abstract PolicySetDAO getPolicySetDAO();

	public abstract RootPolicySetDAO getRootPolicySetDAO();

	public abstract PolicyDAO getPolicyDAO();
}
