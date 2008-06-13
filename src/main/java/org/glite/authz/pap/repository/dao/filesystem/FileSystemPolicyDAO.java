package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicyBuilder;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPolicyDAO implements PolicyDAO {

	public static FileSystemPolicyDAO getInstance() {
		return new FileSystemPolicyDAO();
	}

	private final String policyFileNamePrefix;

	private final PolicyBuilder policyBuilder;

	private FileSystemPolicyDAO() {
		policyFileNamePrefix = RepositoryManager.getPolicyFileNamePrefix();
		policyBuilder = RepositoryManager.getPolicyBuilder();
	}

	public void delete(String papId, String policyId) {
		if (exists(papId, policyId)) {
			File policyFile = new File(RepositoryManager.getPolicyAbsolutePath(
					papId, policyId));
			policyFile.delete();
		}
	}

	public boolean exists(String papId, String policyId) {
		return new File(RepositoryManager
				.getPolicyAbsolutePath(papId, policyId)).exists();
	}

	public List<Policy> getAll(String papId) {
		List<Policy> policyList = new LinkedList<Policy>();
		File papDir = new File(RepositoryManager.getPAPDirAbsolutePath(papId));
		for (File file : papDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			if (file.getName().startsWith(policyFileNamePrefix)) {
				policyList.add(policyBuilder.buildFromFile(file));
			}
		}
		return policyList;
	}

	public Policy getById(String papId, String policyId) {
		File policyFile = new File(RepositoryManager.getPolicyAbsolutePath(
				papId, policyId));
		if (!policyFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: "
					+ policyId);
		}
		return policyBuilder.buildFromFile(policyFile);
	}

	public void store(String papId, Policy policy) {
		if (!exists(papId, policy.getId())) {
			policy.toFile(RepositoryManager.getPolicyAbsolutePath(papId, policy
					.getId()));
		}
	}

	public void update(String papId, Policy policy) {
		File policyFile = new File(RepositoryManager.getPolicyAbsolutePath(
				papId, policy.getId()));
		if (!policyFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: "
					+ policy.getId());
		}
		policy.toFile(policyFile);
	}
}
