package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.xacml.AbstractPolicy;
import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPapDAO implements PAPPolicySetDAO {

	public static FileSystemPapDAO getInstance() {
		return new FileSystemPapDAO();
	}

	private final String dbDir;
	private final PolicySetBuilder policySetBuilder;
	private final FileSystemRootPolicySetDAO rootDAO;
	private final String localPAPId;

	private FileSystemPapDAO() {
		dbDir = RepositoryManager.getFileSystemDatabaseDir();
		policySetBuilder = RepositoryManager.getPolicySetBuilder();
		rootDAO = FileSystemRootPolicySetDAO.getInstance();
		localPAPId = RepositoryManager.getLocalPAPId();
	}

	public void add(int index, PolicySet policySet) {
		createPAP(policySet);
		PolicySet rootPS = rootDAO.get();
		rootPS.addPolicySetReference(0, policySet.getId());
		rootDAO.update(rootPS);
	}

	public void add(PolicySet policySet) {
		createPAP(policySet);
		PolicySet rootPS = rootDAO.get();
		rootPS.addPolicySetReference(policySet.getId());
		rootDAO.update(rootPS);
	}

	public void delete(String papId) {
		if (exists(papId)) {
			PolicySet rootPolicySet = rootDAO.get();
			rootPolicySet.deletePolicySetReference(papId);
			rootDAO.update(rootPolicySet);
			File papDir = new File(getPAPDirAbsolutePath(papId));
			for (File file : papDir.listFiles()) {
				file.delete();
			}
			papDir.delete();
		}
	}

	public void deleteRemoteAll() {
		List<String> papIdList = rootDAO.listPAPIds();
		for (String papId:papIdList) {
			if (!localPAPId.equals(papId)) {
				delete(papId);
			}
		}
	}

	public boolean exists(String papId) {
		return rootDAO.get().referenceIdExists(papId);
	}

	public PolicySet get(String papId) {
		return policySetBuilder.buildFromFile(getPAPFileNameAbsolutePath(papId));
	}

	public List<AbstractPolicy> getTree(String papId) {
		PolicySetDAO policySetDAO = FileSystemPolicySetDAO.getInstance();
		PolicyDAO policyDAO = FileSystemPolicyDAO.getInstance();
		PolicySet papRoot = get(papId);
		
		List<PolicySet> policySetList = policySetDAO.getAll(papId);
		List<Policy> policyList = policyDAO.getAll(papId);
		
		List<AbstractPolicy> papAll = new ArrayList<AbstractPolicy>(1 + policySetList.size() + policyList.size());
		papAll.add(papRoot);
		papAll.addAll(policySetList);
		papAll.addAll(policyList);
		return papAll;
	}

	public void update(String papId, PolicySet newPolicySet) {
		if (!exists(papId)) {
			throw new NotFoundException();
		}
		newPolicySet.toFile(getPAPFileNameAbsolutePath(papId));
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
		papRootPolicySet.toFile(papPolicySetFile.getAbsolutePath());
	}

	private String getPAPDirAbsolutePath(String papId) {
		return dbDir + File.separator + papId;
	}

	private String getPAPFileNameAbsolutePath(String papId) {
		String fileName = RepositoryManager.getPolicySetFileNamePrefix()
				+ papId + RepositoryManager.getXACMLFileNameExtension();
		return getPAPDirAbsolutePath(papId) + File.separator + fileName;
	}
}
