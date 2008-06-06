package org.glite.authz.pap.repository.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.RepositoryConfiguration;
import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.common.xacml.XACMLObject;
import org.glite.authz.pap.repository.AlreadyExistsRepositoryException;
import org.glite.authz.pap.repository.RepositoryException;

public class FileSystemRootPolicySetDAO implements RootPolicySetDAO {

	public static FileSystemRootPolicySetDAO getInstance() {
		return new FileSystemRootPolicySetDAO();
	}

	private final String dbDir;
	private final String rootPolicySetFileNameAbsolutePath;
	private final PolicySetBuilder policySetBuilder;

	private FileSystemRootPolicySetDAO() {
		dbDir = RepositoryConfiguration.getFileSystemDatabaseDir();
		rootPolicySetFileNameAbsolutePath = dbDir + File.separator
				+ RepositoryConfiguration.getPolicySetFileNamePrefix()
				+ RepositoryConfiguration.getRootPolicySetFileName()
				+ RepositoryConfiguration.getXACMLFileNameExtension();
		policySetBuilder = RepositoryConfiguration.getPolicySetBuilder();
		if (!existsRoot()) {
			createRoot();
		}
	}

	public void createPAPAsFirst(PolicySet policySet) {
		createPAP(policySet);
		String papId = policySet.getId();
		PolicySet rootPS = getRoot();
		rootPS.insertPolicySetReferenceAsFirst(papId);
		updateRoot(rootPS);
	}

	public void createRoot() {
		if (!existsRoot()) {
			PolicySet rootPS = policySetBuilder.buildFromFile(RepositoryConfiguration.getRootPolicySetTemplatePath());
			rootPS.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
		} else {
			throw new AlreadyExistsRepositoryException();
		}
	}

	public void deletePAP(String papId) {
		if (existsPAP(papId)) {
			PolicySet rootPolicySet = getRoot();
			rootPolicySet.deletePolicySetReference(papId);
			updateRoot(rootPolicySet);
			File papDir = new File(getPAPDirAbsolutePath(papId));
			for (File file : papDir.listFiles()) {
				file.delete();
			}
			papDir.delete();
		}
	}

	public boolean existsPAP(String papId) {
		PolicySet rootPolicySet = getRoot();
		return rootPolicySet.referenceIdExists(papId);
	}

	public boolean existsRoot() {
		return new File(this.rootPolicySetFileNameAbsolutePath).exists();
	}

	public PolicySet getPAPRoot(String papId) {
		return policySetBuilder.buildFromFile(getPAPFileNameAbsolutePath(papId));
	}

	public List<XACMLObject> getPAPRootAll(String papId) {
		PolicySetDAO policySetDAO = FileSystemPolicySetDAO.getInstance();
		PolicyDAO policyDAO = FileSystemPolicyDAO.getInstance();
		PolicySet papRoot = getPAPRoot(papId);
		
		List<PolicySet> policySetList = policySetDAO.getAll(papId);
		List<Policy> policyList = policyDAO.getAll(papId);
		
		List<XACMLObject> papAll = new ArrayList<XACMLObject>(1 + policySetList.size() + policyList.size());
		papAll.add(papRoot);
		papAll.addAll(policySetList);
		papAll.addAll(policyList);
		return papAll;
	}

	public PolicySet getRoot() {
		return policySetBuilder.buildFromFile(rootPolicySetFileNameAbsolutePath);
	}

	public List<XACMLObject> getRootAll() {
		PolicySet rootPolicySet = getRoot();
		List<String> papIdList = listPAPs();
		List<XACMLObject> rootAll = new LinkedList<XACMLObject>();
		rootAll.add(rootPolicySet);
		for (String id:papIdList) {
			rootAll.addAll(getPAPRootAll(id));
		}
		return rootAll;
	}

	public List<XACMLObject> GetRootAll(String[] papIdList) {
		PolicySet rootPolicySet = getRoot();
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
			rootAll.addAll(getPAPRootAll(requestedPAPId));
		}
		return rootAll;
	}

	public List<String> listPAPs() {
		PolicySet rootPolicySet = getRoot();
		List<XACMLObject> childrenList = rootPolicySet.getOrderedListOfXACMLObjectChildren();
		List<String> papList = new ArrayList<String>(childrenList.size());
		for (XACMLObject child : childrenList) {
			if (child.isPolicySetReference()) {
				papList.add(((IdReference) child).getValue());
			}
		}
		return papList;
	}

	public void updatePAP(String papId, PolicySet ps) {
		if (!existsPAP(papId)) {
			throw new RepositoryException("PAP does not exists");
		}
		ps.printXACMLDOMToFile(getPAPFileNameAbsolutePath(papId));
	}

	private void createPAP(PolicySet papRootPolicySet) {
		String papId = papRootPolicySet.getId();
		if (existsPAP(papId)) {
			throw new AlreadyExistsRepositoryException();
		}
		File directory = new File(getPAPDirAbsolutePath(papId));
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				throw new RepositoryException("Cannot create directory for PAP: " + papId);
			}
		}
		File papPolicySetFile = new File(getPAPFileNameAbsolutePath(papId));
		papRootPolicySet.printXACMLDOMToFile(papPolicySetFile.getAbsolutePath());
	}

	private String getPAPDirAbsolutePath(String papId) {
		return dbDir + File.separator + papId;
	}

	private String getPAPFileNameAbsolutePath(String papId) {
		String fileName = RepositoryConfiguration.getPolicySetFileNamePrefix()
				+ RepositoryConfiguration.getRootPAPPolicySetId()
				+ RepositoryConfiguration.getXACMLFileNameExtension();
		return getPAPDirAbsolutePath(papId) + File.separator + fileName;
	}

	private void updateRoot(PolicySet ps) {
		ps.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
	}
}
