package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemRepositoryManager extends RepositoryManager {
	
	private static final Logger log = LoggerFactory.getLogger( FileSystemRepositoryManager.class );
	protected static final String fileSystemDatabaseDir;
	private static final String rootPolicySetId = "Root";
	private static final String localPAPId;
	private static final String policySetFileNamePrefix = "PolicySet_";
	private static final String policyFileNamePrefix = "Policy_";
	private static final String xacmlFileNameExtension = ".xml";
	
	static {
	    fileSystemDatabaseDir = PAPConfiguration.instance().getPAPRepositoryDir();
	    localPAPId = PAP.localPAPId;
	}
	
	protected void initialize() {
		log.info("Initializing filesystem repository...");
		File rootDir = new File(FileSystemRepositoryManager.getFileSystemDatabaseDir());
		if (!rootDir.exists()) {
			if (!rootDir.mkdirs()) {
				throw new RepositoryException("Cannot create DB root directory: " + rootDir.getAbsolutePath());
			}
		}
		if (!(rootDir.canRead() && rootDir.canWrite())) {
			throw new RepositoryException("Permission denied for root DB dir: " + rootDir.getAbsolutePath());
		}
		log.info("Repository root directory is: " + rootDir.getAbsolutePath());
	}

	public static String getFileSystemDatabaseDir() {
		return fileSystemDatabaseDir;
	}
	
	public static String getLocalPAPId() {
		return localPAPId;
	}
	
	public static String getPAPDirAbsolutePath(String papId) {
		return fileSystemDatabaseDir + File.separator + papId + File.separator;
	}

	public static String getPolicyAbsolutePath(String papId, String policyId) {
		return getPAPDirAbsolutePath(papId) + getPolicyFileName(policyId);
	}

	public static String getPolicyFileName(String policyId) {
		return policyFileNamePrefix + policyId + xacmlFileNameExtension;
	}

	public static String getPolicyFileNamePrefix() {
		return policyFileNamePrefix;
	}

	public static String getPolicySetAbsolutePath(String papId,
			String policySetId) {
		return getPAPDirAbsolutePath(papId) + getPolicySetFileName(policySetId);
	}

	public static String getPolicySetFileName(String policySetId) {
		return policySetFileNamePrefix + policySetId + xacmlFileNameExtension;
	}

	public static String getPolicySetFileNamePrefix() {
		return policySetFileNamePrefix;
	}

	public static String getRootPolicySetId() {
		return rootPolicySetId;
	}

	public static String getXACMLFileNameExtension() {
		return xacmlFileNameExtension;
	}
}
