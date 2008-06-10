package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.common.xacml.XACMLObject;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPapDAO implements PAPPolicySetDAO {

	public static FileSystemPapDAO getInstance() {
		return new FileSystemPapDAO();
	}

	private final String dbDir;
	private final String rootPolicySetFileNameAbsolutePath;
	private final PolicySetBuilder policySetBuilder;

	private FileSystemPapDAO() {
		dbDir = RepositoryManager.getFileSystemDatabaseDir();
		rootPolicySetFileNameAbsolutePath = dbDir + File.separator
				+ RepositoryManager.getPolicySetFileNamePrefix()
				+ RepositoryManager.getRootPolicySetFileName()
				+ RepositoryManager.getXACMLFileNameExtension();
		policySetBuilder = RepositoryManager.getPolicySetBuilder();
	}

	public void createAsFirst(PolicySet policySet) {
		createPAP(policySet);
		String papId = policySet.getId();
		PolicySet rootPS = FileSystemRootPolicySetDAO.getInstance().get();
		rootPS.insertPolicySetReferenceAsFirst(papId);
		updateRoot(rootPS);
	}

	public void create() {
		
	}

	public void delete(String papId) {
		if (exists(papId)) {
			PolicySet rootPolicySet = FileSystemRootPolicySetDAO.getInstance().get();
			rootPolicySet.deletePolicySetReference(papId);
			updateRoot(rootPolicySet);
			File papDir = new File(getPAPDirAbsolutePath(papId));
			for (File file : papDir.listFiles()) {
				file.delete();
			}
			papDir.delete();
		}
	}

	public boolean exists(String papId) {
		PolicySet rootPolicySet = FileSystemRootPolicySetDAO.getInstance().get();
		return rootPolicySet.referenceIdExists(papId);
	}

	public PolicySet get(String papId) {
		return policySetBuilder.buildFromFile(getPAPFileNameAbsolutePath(papId));
	}

	public List<XACMLObject> getAll(String papId) {
		PolicySetDAO policySetDAO = FileSystemPolicySetDAO.getInstance();
		PolicyDAO policyDAO = FileSystemPolicyDAO.getInstance();
		PolicySet papRoot = get(papId);
		
		List<PolicySet> policySetList = policySetDAO.getAll(papId);
		List<Policy> policyList = policyDAO.getAll(papId);
		
		List<XACMLObject> papAll = new ArrayList<XACMLObject>(1 + policySetList.size() + policyList.size());
		papAll.add(papRoot);
		papAll.addAll(policySetList);
		papAll.addAll(policyList);
		return papAll;
	}

	public void update(String papId, PolicySet newPolicySet) {
		if (!exists(papId)) {
			throw new RepositoryException("PAP does not exists");
		}
		newPolicySet.printXACMLDOMToFile(getPAPFileNameAbsolutePath(papId));
	}

	private void createPAP(PolicySet papRootPolicySet) {
		String papId = papRootPolicySet.getId();
		if (exists(papId)) {
			throw new AlreadyExistsException();
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
		String fileName = RepositoryManager.getPolicySetFileNamePrefix()
				+ RepositoryManager.getRootPAPPolicySetId()
				+ RepositoryManager.getXACMLFileNameExtension();
		return getPAPDirAbsolutePath(papId) + File.separator + fileName;
	}

	private void updateRoot(PolicySet ps) {
		ps.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
	}
}
