package org.glite.authz.pap.repository;

import org.glite.authz.pap.repository.dao.DAOFactory;

public abstract class RepositoryManager {

	public static DAOFactory getDAOFactory() {
		return DAOFactory.getDAOFactory();
	}
	
	public static RepositoryManager getInstance() {
		return new FileSystemRepositoryManager();
	}
	
	public static PAPManager getPAPManager() {
		return FileSystemPAPManager.getInstance();
	}
	
	public abstract void bootstrap();

}
