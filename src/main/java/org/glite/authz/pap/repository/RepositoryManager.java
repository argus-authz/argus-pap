package org.glite.authz.pap.repository;

import java.io.File;

import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryManager {

	private static final Logger logger = LoggerFactory.getLogger( RepositoryManager.class );
	private static final String fileSystemDatabaseDir = "/tmp/paprep";
	private static final String rootPolicySetId = "Root";
	private static final String localPAPId = "Local";
	private static final String policySetFileNamePrefix = "PolicySet_";
	private static final String policyFileNamePrefix = "Policy_";
	private static final String xacmlFileNameExtension = ".xml";
	
	private static RepositoryManager instance = null;
	
	public static String getFileSystemDatabaseDir() {
		return fileSystemDatabaseDir;
	}
	
	public static RepositoryManager getInstance() {
		if (instance == null) {
			instance = new RepositoryManager();
		}
		return instance;
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

	private RepositoryManager() { }

	public static void bootstrap() {
		logger.info("Starting PolicyRepository manager: filesystem implementation...");
		bootstrapFileSystemDB();
	}

	private static void bootstrapFileSystemDB() {
		File rootDir = new File(getFileSystemDatabaseDir());
		if (!rootDir.exists()) {
			if (!rootDir.mkdirs()) {
				throw new RepositoryException("Cannot create DB dir");
			}
		}
		if (!(rootDir.canRead() && rootDir.canWrite())) {
			throw new RepositoryException("Permission denied for DB dir");
		}
	}
}
