package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicyType;

public class FileSystemPolicyDAO implements PolicyDAO {
	
	public static FileSystemPolicyDAO getInstance() {
		return new FileSystemPolicyDAO();
	}

	private final PolicyHelper policyHelper;

	private final String policyFileNamePrefix;

	private FileSystemPolicyDAO() {
		policyFileNamePrefix = RepositoryManager.getPolicyFileNamePrefix();
		policyHelper = PolicyHelper.getInstance();
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

	public List<PolicyType> getAll(String papId) {
		List<PolicyType> policyList = new LinkedList<PolicyType>();
		File papDir = new File(RepositoryManager.getPAPDirAbsolutePath(papId));
		for (File file : papDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			if (file.getName().startsWith(policyFileNamePrefix)) {
				policyList.add(policyHelper.buildFromFile(file));
			}
		}
		return policyList;
	}

	public PolicyType getById(String papId, String policyId) {
		File policyFile = new File(RepositoryManager.getPolicyAbsolutePath(
				papId, policyId));
		if (!policyFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: "
					+ policyId);
		}
		return policyHelper.buildFromFile(policyFile);
	}

	public void store(String papId, PolicyType policy) {
		if (!exists(papId, policy.getPolicyId())) {
			policyHelper.toFile(RepositoryManager.getPolicyAbsolutePath(papId, policy.getPolicyId()), policy);
		}
	}

	public void update(String papId, PolicyType policy) {
		File policyFile = new File(RepositoryManager.getPolicyAbsolutePath(
				papId, policy.getPolicyId()));
		if (!policyFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: "
					+ policy.getPolicyId());
		}
		policyHelper.toFile(policyFile, policy);
	}
}
