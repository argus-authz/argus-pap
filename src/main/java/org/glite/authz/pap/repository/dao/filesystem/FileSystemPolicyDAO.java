package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemPolicyDAO implements PolicyDAO {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FileSystemPolicyDAO.class);
	private static final String policyFileNamePrefix = FileSystemRepositoryManager.getPolicyFileNamePrefix();
	private static final PolicyHelper policyHelper = PolicyHelper.getInstance();

	// TODO: maybe it's better to create different exception classes instead of different exception messages
	private static String policyExceptionMsg(String policyId) {
		return String.format("policyId=\"%s\"", policyId);
	}

	private static String papDirNotFoundExceptionMsg(String papDirPAth) {
		return "Not found PAP directory: " + papDirPAth;
	}

	private static String policyNotFoundExceptionMsg(String policyId) {
		String msg = "Not found: " + policyExceptionMsg(policyId);
		return msg;
	}

	private FileSystemPolicyDAO() {}

	public static FileSystemPolicyDAO getInstance() {
		return new FileSystemPolicyDAO();
	}

	public void delete(String papId, String policyId) {
		String policyFileName = FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId);

		if (exists(papId, policyId)) {

			File policyFile = new File(policyFileName);

			if (!policyFile.delete()) {
				throw new RepositoryException("Cannot delete file: " + policyFile.getAbsolutePath());
			}

		} else {
			throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
		}
	}

	public void deleteAll(String papId) {

		File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

		if (!papDir.exists())
			throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

		for (File file : papDir.listFiles()) {

			if (file.isDirectory())
				continue;

			if (file.getName().startsWith(policyFileNamePrefix)) {
				file.delete();
			}
		}
	}

	public boolean exists(String papId, String policyId) {

		String policyFilePath = FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId);

		File policyFile = new File(policyFilePath);

		return policyFile.exists();
	}

	public List<PolicyType> getAll(String papId) {

		File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
		if (!papDir.exists())
			throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

		List<PolicyType> policyList = new LinkedList<PolicyType>();

		for (File file : papDir.listFiles()) {

			if (file.isDirectory())
				continue;

			if (file.getName().startsWith(policyFileNamePrefix)) {
				policyList.add(policyHelper.buildFromFile(file));
			}
		}
		return policyList;
	}

	public PolicyType getById(String papId, String policyId) {

		File policyFile = new File(FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId));

		if (!exists(papId, policyId)) {
			throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
		}

		return policyHelper.buildFromFile(policyFile);
	}

	public void store(String papId, PolicyType policy) {

		File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
		
		if (!papDir.exists())
			throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

		String policyId = policy.getPolicyId();

		if (exists(papId, policyId))
			throw new AlreadyExistsException("Already exists: policyId=" + policyId);

		String policyFileName = FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId);

		PolicyHelper.toFile(policyFileName, policy);
	}

	public void update(String papId, PolicyType policy) {

		String policyId = policy.getPolicyId();

		File policyFile = new File(FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId));

		if (!exists(papId, policyId))
			throw new NotFoundException(policyNotFoundExceptionMsg(policyId));

		PolicyHelper.toFile(policyFile, policy);
	}
}
