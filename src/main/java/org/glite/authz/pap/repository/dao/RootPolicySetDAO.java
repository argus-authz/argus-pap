package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.repository.AlreadyExistsRepositoryException;
import org.glite.authz.pap.repository.RepositoryException;

public interface RootPolicySetDAO {

	/**
	 * @param papId
	 * @throws RepositoryException
	 * @throws AlreadyExistsRepositoryException
	 */
	public void createPAPAsFirst(String papId);

	public void createRoot();

	public void deletePAP(String papId);

	public boolean existsPAP(String papId);

	public boolean existsRoot();

	public PolicySet getPAPRoot(String papId);

	public PolicySet getRoot();

	public List<String> listPAPs();

	public void updatePAP(String papId, PolicySet ps);

}
