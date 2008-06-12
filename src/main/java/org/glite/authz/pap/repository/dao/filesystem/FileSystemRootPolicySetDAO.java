package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.AbstractPolicy;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;

public class FileSystemRootPolicySetDAO implements RootPolicySetDAO {
	
	public static FileSystemRootPolicySetDAO getInstance() {
		return new FileSystemRootPolicySetDAO();
	}

	private final String dbDir;
	private final String rootPolicySetFileNameAbsolutePath;
	private final PolicySetBuilder policySetBuilder;

	private FileSystemRootPolicySetDAO() {
		dbDir = RepositoryManager.getFileSystemDatabaseDir();
		rootPolicySetFileNameAbsolutePath = dbDir + File.separator
				+ RepositoryManager.getPolicySetFileNamePrefix()
				+ RepositoryManager.getRootPolicySetId()
				+ RepositoryManager.getXACMLFileNameExtension();
		policySetBuilder = RepositoryManager.getPolicySetBuilder();
		if (!exists()) {
			create();
		}
	}

	public void create() {
		if (!exists()) {
			PolicySet rootPS = policySetBuilder.build(RepositoryManager.getRootPolicySetId(), PolicySet.COMB_ALG_FIRST_APPLICABLE); 
			rootPS.toFile(this.rootPolicySetFileNameAbsolutePath);
		} else {
			throw new AlreadyExistsException();
		}
	}

	public boolean exists() {
		return new File(this.rootPolicySetFileNameAbsolutePath).exists();
	}

	public PolicySet get() {
		return policySetBuilder.buildFromFile(rootPolicySetFileNameAbsolutePath);
	}

	public List<AbstractPolicy> getAll() {
		PolicySet rootPolicySet = get();
		List<String> papIdList = listPAPIds();
		List<AbstractPolicy> rootAll = new LinkedList<AbstractPolicy>();
		rootAll.add(rootPolicySet);
		for (String id:papIdList) {
			rootAll.addAll(FileSystemPapDAO.getInstance().getAll(id));
		}
		return rootAll;
	}

	public List<AbstractPolicy> getByPAPId(String[] papIdList) {
		PolicySet rootPolicySet = get();
		for (String id:listPAPIds()) {
			boolean found = false;
			for (String requestedId:papIdList) {
				if (requestedId.equals(id)) {
					found = true;
					break;
				}
			}
			if (!found) {
				rootPolicySet.deletePolicySetReference(id);
			}
		}
		List<AbstractPolicy> rootAll = new LinkedList<AbstractPolicy>();
		rootAll.add(rootPolicySet);
		for (String requestedPAPId:papIdList) {
			rootAll.addAll(FileSystemPapDAO.getInstance().getAll(requestedPAPId));
		}
		return rootAll;
	}

	public List<String> listPAPIds() {
		return get().getPolicySetIdReferencesValues();
	}

}
