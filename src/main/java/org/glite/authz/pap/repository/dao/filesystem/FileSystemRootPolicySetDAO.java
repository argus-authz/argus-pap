package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.common.xacml.XACMLObject;
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
				+ RepositoryManager.getRootPolicySetFileName()
				+ RepositoryManager.getXACMLFileNameExtension();
		policySetBuilder = RepositoryManager.getPolicySetBuilder();
		if (!exists()) {
			create();
		}
	}

	public void create() {
		if (!exists()) {
			PolicySet rootPS = policySetBuilder.buildFromFile(RepositoryManager.getRootPolicySetTemplatePath());
			rootPS.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
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

	public List<XACMLObject> getAll() {
		PolicySet rootPolicySet = get();
		List<String> papIdList = listPAPs();
		List<XACMLObject> rootAll = new LinkedList<XACMLObject>();
		rootAll.add(rootPolicySet);
		for (String id:papIdList) {
			rootAll.addAll(FileSystemPapDAO.getInstance().getAll(id));
		}
		return rootAll;
	}

	public List<XACMLObject> getAllByPAPId(String[] papIdList) {
		PolicySet rootPolicySet = get();
		for (String id:listPAPs()) {
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
		List<XACMLObject> rootAll = new LinkedList<XACMLObject>();
		rootAll.add(rootPolicySet);
		for (String requestedPAPId:papIdList) {
			rootAll.addAll(FileSystemPapDAO.getInstance().getAll(requestedPAPId));
		}
		return rootAll;
	}

	public List<String> listPAPs() {
		PolicySet rootPolicySet = get();
		List<XACMLObject> childrenList = rootPolicySet.getOrderedListOfXACMLObjectChildren();
		List<String> papList = new ArrayList<String>(childrenList.size());
		for (XACMLObject child : childrenList) {
			if (child.isPolicySetReference()) {
				papList.add(((IdReference) child).getValue());
			}
		}
		return papList;
	}

}
