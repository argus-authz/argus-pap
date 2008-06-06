
package org.glite.authz.pap.repository.dao;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.RepositoryConfiguration;
import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicyBuilder;
import org.glite.authz.pap.common.xacml.PolicyImpl;
import org.glite.authz.pap.repository.RepositoryException;

public class FileSystemPolicyDAO implements PolicyDAO {
	
	private final String policyFileNamePrefix;
	private final PolicyBuilder policyBuilder;
	
	public static FileSystemPolicyDAO getInstance() {
		return new FileSystemPolicyDAO();
	}

	private FileSystemPolicyDAO() {
		policyFileNamePrefix = RepositoryConfiguration.getPolicyFileNamePrefix();
		policyBuilder = RepositoryConfiguration.getPolicyBuilder();
	}

	public void delete(String papId, String policyId) {
		if (exists(papId, policyId)) {
			File policyFile = new File(RepositoryConfiguration.getPolicyAbsolutePath(papId, policyId));
			policyFile.delete();
		}
	}

	public boolean exists(String papId, String policyId) {
		return new File(RepositoryConfiguration.getPolicyAbsolutePath(papId, policyId)).exists();
	}

	public List<Policy> getAll(String papId) {
		List<Policy> policyList = new LinkedList<Policy>();
		File papDir = new File(RepositoryConfiguration.getPAPDirAbsolutePath(papId));
		for (File file:papDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			if (file.getName().startsWith(policyFileNamePrefix)) {
				policyList.add(policyBuilder.buildFromFile(file));
			}
		}
		return null;
	}

	public Policy getById(String papId, String policyId) {
		File policyFile = new File(RepositoryConfiguration.getPolicyAbsolutePath(papId, policyId));
		if (!policyFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: " + policyId);
		}
		return policyBuilder.buildFromFile(policyFile);
	}

	public void store(String papId, Policy policy) {
		if (!exists(papId, policy.getId())) {
			policy.printXACMLDOMToFile(RepositoryConfiguration.getPolicyAbsolutePath(papId, policy.getId()));
		}
	}

	public void update(String papId, Policy policy) {
		File policyFile = new File(RepositoryConfiguration.getPolicyAbsolutePath(papId, policy.getId()));
		if (!policyFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: " + policy.getId());
		}
		policy.printXACMLDOMToFile(policyFile);
	}
}
