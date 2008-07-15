package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemPolicySetDAO implements PolicySetDAO {
    
    private static final Logger log = LoggerFactory.getLogger( FileSystemPolicySetDAO.class );
	
	public static FileSystemPolicySetDAO getInstance() {
		return new FileSystemPolicySetDAO();
	}

	private final String policySetFileNamePrefix;
	private final PolicySetHelper policySetHelper;

	private FileSystemPolicySetDAO() {
		policySetFileNamePrefix = FileSystemRepositoryManager.getPolicySetFileNamePrefix();
		policySetHelper = PolicySetHelper.getInstance();
	}
	
	public void delete(String papId, String policySetId) {
		File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId));
		if (!policySetFile.delete()) {
			throw new RepositoryException("Cannot delete policyset: " + policySetId);
		}
	}

	public void deleteAll(String papId) {
		File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
		for (File file:papDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			if (file.getName().startsWith(policySetFileNamePrefix)) {
				file.delete();
			}
		}
	}

	public boolean exists(String papId, String policySetId) {
		return new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId)).exists();
	}
	
	public List<PolicySetType> getAll(String papId) {
		List<PolicySetType> policySetList = new LinkedList<PolicySetType>();
		File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
		for (File file:papDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			if (file.getName().startsWith(policySetFileNamePrefix)) {
				policySetList.add(policySetHelper.buildFromFile(file));
			}
		}
		return policySetList;
	}
	
	public PolicySetType getById(String papId, String policySetId) {
		File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId));
		if (!policySetFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: " + policySetId);
		}
		return policySetHelper.buildFromFile(policySetFile);
	}
	
	public void store(String papId, PolicySetType ps) {
		if (!exists(papId, ps.getPolicySetId())) {
		    log.debug("Storing PolicySet \"" + ps.getPolicySetId() + "\" into PAP \"" + papId + "\"");
			policySetHelper.toFile(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, ps.getPolicySetId()), ps);
		}
	}

	public void update(String papId, PolicySetType ps) {
		File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, ps.getPolicySetId()));
		if (!policySetFile.exists()) {
			throw new RepositoryException("PolicySet does not exist: " + ps.getPolicySetId());
		}
		policySetHelper.toFile(policySetFile, ps);
	}
}
