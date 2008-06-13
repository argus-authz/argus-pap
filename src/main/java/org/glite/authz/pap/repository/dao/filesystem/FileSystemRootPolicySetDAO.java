package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;

public class FileSystemRootPolicySetDAO implements RootPolicySetDAO {
	
	public static FileSystemRootPolicySetDAO getInstance() {
		return new FileSystemRootPolicySetDAO();
	}

	private final String dbDir;
	private final String rootPolicySetFileNameAbsolutePath;
	private final PolicySetHelper policySetHelper;

	private FileSystemRootPolicySetDAO() {
		dbDir = RepositoryManager.getFileSystemDatabaseDir();
		rootPolicySetFileNameAbsolutePath = dbDir + File.separator
				+ RepositoryManager.getPolicySetFileNamePrefix()
				+ RepositoryManager.getRootPolicySetId()
				+ RepositoryManager.getXACMLFileNameExtension();
		policySetHelper = PolicySetHelper.getInstance();
		if (!exists()) {
			create();
		}
	}

	public void create() {
		if (!exists()) {
			PolicySetType rootPolicySet = PolicySetHelper.build(RepositoryManager.getRootPolicySetId(), PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
			policySetHelper.toFile(this.rootPolicySetFileNameAbsolutePath, rootPolicySet);
			//PolicySet rootPS = policySetBuilder.build(RepositoryManager.getRootPolicySetId(), PolicySet.COMB_ALG_FIRST_APPLICABLE); 
			//rootPS.toFile(this.rootPolicySetFileNameAbsolutePath);
		} else {
			throw new AlreadyExistsException();
		}
	}

	public boolean exists() {
		return new File(this.rootPolicySetFileNameAbsolutePath).exists();
	}

	public PolicySetType get() {
		return policySetHelper.buildFromFile(rootPolicySetFileNameAbsolutePath);
	}

	public List<XACMLObject> getPartialTreeAsList(String[] papIdList) {
		PolicySetType rootPolicySet = get();
		for (String id:listPAPIds()) {
			boolean found = false;
			for (String requestedId:papIdList) {
				if (requestedId.equals(id)) {
					found = true;
					break;
				}
			}
			if (!found) {
				PolicySetHelper.deletePolicySetReference(rootPolicySet, id);
			}
		}
		PAPPolicySetDAO papDAO = FileSystemPAPDAO.getInstance();
		List<XACMLObject> rootAll = new LinkedList<XACMLObject>();
		rootAll.add(rootPolicySet);
		for (String requestedPAPId:papIdList) {
			rootAll.addAll(papDAO.getTree(requestedPAPId));
		}
		return rootAll;
	}

	public List<XACMLObject> getTreeAsList() {
		PAPPolicySetDAO papDAO = FileSystemPAPDAO.getInstance();
		PolicySetType rootPolicySet = get();
		List<String> papIdList = listPAPIds();
		List<XACMLObject> rootAll = new LinkedList<XACMLObject>();
		rootAll.add(rootPolicySet);
		for (String id:papIdList) {
			rootAll.addAll(papDAO.getTree(id));
		}
		return rootAll;
	}

	public List<String> listPAPIds() {
		return PolicySetHelper.getPolicySetIdReferencesValues(get());
	}

	public void update(PolicySetType newPolicySet) {
		if (!exists()) {
			throw new NotFoundException();
		}
		policySetHelper.toFile(rootPolicySetFileNameAbsolutePath, newPolicySet);
	}
}
