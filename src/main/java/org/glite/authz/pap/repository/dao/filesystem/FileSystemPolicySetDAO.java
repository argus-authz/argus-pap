package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPolicySetDAO implements PolicySetDAO {
	
	public static FileSystemPolicySetDAO getInstance() {
		return new FileSystemPolicySetDAO();
	}

	private final String policySetFileNamePrefix;
	private final PolicySetBuilder policySetBuilder;

	private FileSystemPolicySetDAO() {
		policySetFileNamePrefix = RepositoryManager.getPolicySetFileNamePrefix();
		policySetBuilder = RepositoryManager.getPolicySetBuilder();
	}
	
	public void delete(String papId, String policySetId) {
		File policySetFile = new File(RepositoryManager.getPolicySetAbsolutePath(papId, policySetId));
		if (!policySetFile.delete()) {
			throw new RepositoryException("Cannot delete policyset: " + policySetId);
		}
	}

	public boolean exists(String papId, String policySetId) {
		return new File(RepositoryManager.getPolicySetAbsolutePath(papId, policySetId)).exists();
	}

	public List<PolicySet> getAll(String papId) {
		List<PolicySet> policySetList = new LinkedList<PolicySet>();
		File papDir = new File(RepositoryManager.getPAPDirAbsolutePath(papId));
		for (File file:papDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			if (file.getName().startsWith(policySetFileNamePrefix)) {
				System.out.println("Reading: " + file.getName());
				policySetList.add(policySetBuilder.buildFromFile(file));
			}
		}
		return policySetList;
	}
	
	public PolicySet getById(String papId, String policySetId) {
		File policySetFile = new File(RepositoryManager.getPolicySetAbsolutePath(papId, policySetId));
		if (!policySetFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: " + policySetId);
		}
		return policySetBuilder.buildFromFile(policySetFile);
	}
	
	public void store(String papId, PolicySet ps) {
		if (!exists(papId, ps.getId())) {
			ps.printXACMLDOMToFile(RepositoryManager.getPolicySetAbsolutePath(papId, ps.getId()));
		}
	}
	
	public void update(String papId, PolicySet ps) {
		File policySetFile = new File(RepositoryManager.getPolicySetAbsolutePath(papId, ps.getId()));
		if (!policySetFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: " + ps.getId());
		}
		ps.printXACMLDOMToFile(policySetFile);
	}
}
