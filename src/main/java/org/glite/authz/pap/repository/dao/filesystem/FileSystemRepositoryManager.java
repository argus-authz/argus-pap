package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.io.IOException;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemRepositoryManager extends RepositoryManager {

    private static final Logger log = LoggerFactory.getLogger(FileSystemRepositoryManager.class);
    private static final String rootPolicySetId = "Root";
    private static final String localPAPId = PAP.localPAPId;
    private static final String policySetFileNamePrefix = "PolicySet_";
    private static final String policyFileNamePrefix = "Policy_";
    private static final String xacmlFileNameExtension = ".xml";
    // TODO: this variable should be final too... but it is not possible with
    // the current definition of the PAPConfiguration class.
    protected static String fileSystemDatabaseDir;

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

    public static String getPolicySetAbsolutePath(String papId, String policySetId) {
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

    public FileSystemRepositoryManager() {
        fileSystemDatabaseDir = PAPConfiguration.instance().getPAPRepositoryDir();
    }

    private void createDirectoryPath(File dir) {

        if (!dir.exists()) {
            if (!dir.mkdirs()) // Find out what went wrong...
                createDirectoryPath(dir.getParentFile());
        }

        if (!dir.canRead())
            throw new RepositoryException("Read permission not set: " + dir.getAbsolutePath());

        if (!dir.canWrite())
            throw new RepositoryException("Write permission not set: " + dir.getAbsolutePath());

        // Workaround for the canExecute method which does not exist in Java 5
        try {
            File tempFile = new File(dir.getAbsoluteFile() + File.separator + "delete_me.tmp");
            tempFile.createNewFile();
            tempFile.delete();
        } catch (IOException e) {
            throw new RepositoryException("Execute permission not set: " + dir.getAbsolutePath(), e);
        }
    }

    protected void initialize() {
        log.info("Initializing filesystem repository...");

        File rootDir = new File(FileSystemRepositoryManager.getFileSystemDatabaseDir());

        try {
            createDirectoryPath(rootDir);
        } catch (RepositoryException e) {
            throw new RepositoryException("Cannot create the repository root directory: "
                    + rootDir.getAbsolutePath(), e);
        }

        log.info("Repository root directory is set to: " + rootDir.getAbsolutePath());
    }

}
