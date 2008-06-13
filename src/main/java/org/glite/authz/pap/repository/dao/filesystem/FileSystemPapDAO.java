package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class FileSystemPapDAO implements PAPPolicySetDAO {

	public static FileSystemPapDAO getInstance() {
		return new FileSystemPapDAO();
	}

	private final String dbDir;
	private final FileSystemRootPolicySetDAO rootDAO;
	private final String localPAPId;
	private final PolicySetHelper policySetHelper;

	private FileSystemPapDAO() {
		dbDir = RepositoryManager.getFileSystemDatabaseDir();
		rootDAO = FileSystemRootPolicySetDAO.getInstance();
		localPAPId = RepositoryManager.getLocalPAPId();
		policySetHelper = PolicySetHelper.getInstance();
	}

	public void add(int index, PolicySetType policySet) {
		createPAP(policySet);
		PolicySetType rootPS = rootDAO.get();
		PolicySetHelper.addPolicySetReference(rootPS, index, policySet.getPolicySetId());
		rootDAO.update(rootPS);
	}

	public void add(PolicySetType policySet) {
		createPAP(policySet);
		PolicySetType rootPS = rootDAO.get();
		PolicySetHelper.addPolicySetReference(rootPS, policySet.getPolicySetId());
		rootDAO.update(rootPS);
	}

	public void delete(String papId) {
		if (exists(papId)) {
			PolicySetType rootPolicySet = rootDAO.get();
			PolicySetHelper.deletePolicySetReference(rootPolicySet, papId);
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
		return PolicySetHelper.referenceIdExists(rootDAO.get(), papId);
	}

	public PolicySetType get(String papId) {
		return policySetHelper.buildFromFile(getPAPFileNameAbsolutePath(papId));
	}

	public List<XACMLObject> getTree(String papId) {
		PolicySetDAO policySetDAO = FileSystemPolicySetDAO.getInstance();
		PolicyDAO policyDAO = FileSystemPolicyDAO.getInstance();
		PolicySetType papRoot = get(papId);
		
		List<PolicySetType> policySetList = policySetDAO.getAll(papId);
		List<PolicyType> policyList = policyDAO.getAll(papId);
		
		List<XACMLObject> papAll = new ArrayList<XACMLObject>(1 + policySetList.size() + policyList.size());
		papAll.add(papRoot);
		papAll.addAll(policySetList);
		papAll.addAll(policyList);
		return papAll;
	}

	public void update(String papId, PolicySetType newPolicySet) {
		if (!exists(papId)) {
			throw new NotFoundException();
		}
		policySetHelper.toFile(getPAPFileNameAbsolutePath(papId), newPolicySet);
	}

	private void createPAP(PolicySetType papRootPolicySet) {
		String papId = papRootPolicySet.getPolicySetId();
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
		policySetHelper.toFile(papPolicySetFile.getAbsolutePath(), papRootPolicySet);
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
