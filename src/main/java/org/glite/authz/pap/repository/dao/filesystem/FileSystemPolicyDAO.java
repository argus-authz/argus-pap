package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.glite.authz.pap.common.xacml.impl.PolicyTypeString;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filesystem implementation of the {@link PolicyDAO} interface.
 * <p>
 * This DAO stores information about the policies of a pap. The name of the file of the policies
 * follows that form: <i>prefix</i> + <i>policyId</i> + .<i>extension</i>
 * The value for <i>prefix</i> is: {@link FileSystemRepositoryManager#POLICYSET_FILENAME_PREFIX}.<br>
 * The value for <i>extension</i> is: {@link FileSystemRepositoryManager#XACML_FILENAME_EXTENSION}.<br>
 */
public class FileSystemPolicyDAO implements PolicyDAO {

    private static final Logger log = LoggerFactory.getLogger(FileSystemPolicyDAO.class);

    /** Cache of the policies */
    private static final Map<String, Map<String, PolicyTypeString>> cache = new ConcurrentHashMap<String, Map<String, PolicyTypeString>>();

    private static final PolicyHelper policyHelper = PolicyHelper.getInstance();
    private static FileSystemPolicyDAO instance = null;

    private FileSystemPolicyDAO() {}

    public static FileSystemPolicyDAO getInstance() {
        if (instance == null) {
            instance = new FileSystemPolicyDAO();
        }
        return instance;
    }

    /**
     * Returns the absolute pathname string of the policy file.
     * 
     * @param papId pap id containing the policy.
     * @param policyId the policy id.
     * @return the absolute pathname string.
     */
    private static String getPolicyFileAbsolutePath(String papId, String policyId) {
        return FileSystemRepositoryManager.getPAPDirAbsolutePath(papId) + getPolicyFileName(policyId);
    }

    /**
     * Returns the file name of the policy from the policy id.
     * 
     * @param policyId the policy id.
     * @return the policy file name (just the name, not the absolute path).
     */
    private static String getPolicyFileName(String policyId) {
        return FileSystemRepositoryManager.POLICY_FILENAME_PREFIX + policyId + FileSystemRepositoryManager.XACML_FILENAME_EXTENSION;
    }

    /**
     * Returns the <code>policyId</code> from the file name of the policy.
     * 
     * @param fileName the file name.
     * @return the policy id.
     */
    private static String getPolicyIdFromFileName(String fileName) {
        int start = FileSystemRepositoryManager.POLICY_FILENAME_PREFIX.length();
        int end = fileName.length() - FileSystemRepositoryManager.XACML_FILENAME_EXTENSION.length();
        return fileName.substring(start, end);
    }

    private static String papDirNotFoundExceptionMsg(String papDirPAth) {
        return "Not found PAP directory: " + papDirPAth;
    }

    private static String policyExceptionMsg(String policyId) {
        return String.format("policyId=%s", policyId);
    }

    private static String policyNotFoundExceptionMsg(String policyId) {
        String msg = "Not found: " + policyExceptionMsg(policyId);
        return msg;
    }

    /**
     * {@Inherited}
     */
    public synchronized void delete(String papId, String policyId) {

        Map<String, PolicyTypeString> papCache = cache.get(papId);

        if (papCache != null) {
            papCache.remove(policyId);
        }

        String policyFileName = getPolicyFileAbsolutePath(papId, policyId);

        if (exists(papId, policyId)) {

            File policyFile = new File(policyFileName);

            if (papCache.size() == 0) {
                cache.remove(papId);
            }

            if (!policyFile.delete()) {
                throw new RepositoryException("Cannot delete file: " + policyFile.getAbsolutePath());
            }

        } else {
            throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
        }
    }

    /**
     * {@Inherited}
     */
    public synchronized void deleteAll(String papId) {

        Map<String, PolicyTypeString> papCache = cache.get(papId);

        if (papCache != null) {
            papCache.clear();
            cache.remove(papId);
        }

        String papDirAbsolutePath = FileSystemRepositoryManager.getPAPDirAbsolutePath(papId);

        File papDir = new File(papDirAbsolutePath);

        if (!papDir.exists()) {
            throw new NotFoundException(papDirNotFoundExceptionMsg(papDirAbsolutePath));
        }

        for (File file : papDir.listFiles()) {

            if (file.isDirectory()) {
                continue;
            }

            String fileName = file.getName();
            if (fileName.startsWith(FileSystemRepositoryManager.POLICY_FILENAME_PREFIX)) {
                file.delete();
            }
        }
    }

    /**
     * {@Inherited}
     */
    public synchronized boolean exists(String papId, String policyId) {

        String policyFilePath = getPolicyFileAbsolutePath(papId, policyId);

        File policyFile = new File(policyFilePath);

        return policyFile.exists();
    }

    /**
     * {@Inherited}
     */
    public synchronized List<PolicyType> getAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists()) {
            throw new NotFoundException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));
        }

        Map<String, PolicyTypeString> papCache = getPapCache(papId);

        List<PolicyType> policyList = new LinkedList<PolicyType>();

        for (File file : papDir.listFiles()) {

            if (file.isDirectory()) {
                continue;
            }

            String fileName = file.getName();

            if (fileName.startsWith(FileSystemRepositoryManager.POLICY_FILENAME_PREFIX)) {

                String policyId = getPolicyIdFromFileName(fileName);

                PolicyTypeString policy = papCache.get(policyId);

                if (policy == null) {
                    try {
                        policy = new PolicyTypeString(policyId, policyHelper.readFromFileAsString(file));
                    } catch (Throwable e) {
                        throw new RepositoryException(e);
                    }
                    papCache.put(policyId, policy);
                }
                policyList.add(new PolicyTypeString(policyId, policy.getPolicyString()));

                if (policy.isDOMLoaded()) {
                    log.warn("DOM not released for Policy id=" + policyId);
                }
            }
        }
        return policyList;
    }

    /**
     * {@Inherited}
     */
    public synchronized PolicyType getById(String papId, String policyId) {

        Map<String, PolicyTypeString> papCache = getPapCache(papId);

        PolicyTypeString policy = papCache.get(policyId);

        if (policy == null) {

            File policyFile = new File(getPolicyFileAbsolutePath(papId, policyId));

            if (!policyFile.exists()) {
                if (papCache.size() == 0) {
                    cache.remove(papId);
                }
                throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
            }

            try {
                policy = new PolicyTypeString(policyId, policyHelper.readFromFileAsString(policyFile));
            } catch (Throwable e) {
                throw new RepositoryException(e);
            }

            papCache.put(policyId, policy);
        }

        if (policy.isDOMLoaded()) {
            log.warn("DOM not released for id=" + policyId);
        }
        return new PolicyTypeString(policyId, policy.getPolicyString());
    }

    /**
     * {@Inherited}
     */
    public synchronized void store(String papId, PolicyType policy) {

        PolicyTypeString policyTypeString = TypeStringUtils.cloneAsPolicyTypeString(policy);

        TypeStringUtils.releaseUnneededMemory(policy);

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists()) {
            throw new NotFoundException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));
        }

        String policyId = policyTypeString.getPolicyId();

        File policyFile = new File(getPolicyFileAbsolutePath(papId, policyId));

        if (policyFile.exists()) {
            throw new AlreadyExistsException("Already exists: policyId=" + policyId);
        }

        PolicyHelper.toFile(policyFile, policyTypeString);

        getPapCache(papId).put(policyId, policyTypeString);

        TypeStringUtils.releaseUnneededMemory(policyTypeString);

        log.debug("Stored policy: " + policyId);
    }

    /**
     * {@Inherited}
     */
    public synchronized void update(String papId, String version, PolicyType newPolicy) {

        PolicyTypeString newPolicyTypeString = TypeStringUtils.cloneAsPolicyTypeString(newPolicy);

        TypeStringUtils.releaseUnneededMemory(newPolicy);

        String policyId = newPolicyTypeString.getPolicyId();

        File policyFile = new File(getPolicyFileAbsolutePath(papId, policyId));
        if (!policyFile.exists()) {
            throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
        }

        Map<String, PolicyTypeString> papCache = getPapCache(papId);

        PolicyTypeString oldPolicy = papCache.get(policyId);

        if (oldPolicy == null) {
            try {
                oldPolicy = new PolicyTypeString(policyHelper.buildFromFile(policyFile));
            } catch (Throwable e) {
                throw new RepositoryException(e);
            }
        }

        if (!(oldPolicy.getVersion().equals(version))) {
            throw new InvalidVersionException(String.format("Attempting to update the wrong version of PolicyId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                                                            policyId,
                                                            version,
                                                            oldPolicy.getVersion()));
        }

        TypeStringUtils.releaseUnneededMemory(oldPolicy);

        PolicyHelper.toFile(policyFile, newPolicyTypeString);

        papCache.put(policyId, newPolicyTypeString);

        TypeStringUtils.releaseUnneededMemory(newPolicyTypeString);
    }

    /**
     * Returns the cached policies of a pap.
     * 
     * @param papId <code>id</code> of the pap.
     * @return {@code Map<String, PolicyTypeString>} where the <code>key</code> is the policyId and
     *         the <code>value</code> is the policy.
     */
    private Map<String, PolicyTypeString> getPapCache(String papId) {
        Map<String, PolicyTypeString> papCache = cache.get(papId);

        if (papCache == null) {
            log.debug("New HashMap for papId=" + papId);
            papCache = new ConcurrentHashMap<String, PolicyTypeString>();
            cache.put(papId, papCache);
        }
        return papCache;
    }
}
