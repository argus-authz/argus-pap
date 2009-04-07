package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.glite.authz.pap.common.xacml.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filesystem implementation of the {@link PolicySetDAO} interface.
 * <p>
 * This DAO stores information about the policy sets of a pap. The name of the file of the policy
 * sets follows that form: <i>prefix</i> + <i>policySetId</i> + .<i>extension</i><br>
 * The value for <i>prefix</i> is: {@link FileSystemRepositoryManager#POLICYSET_FILENAME_PREFIX}.<br>
 * The value for <i>extension</i> is: {@link FileSystemRepositoryManager#XACML_FILENAME_EXTENSION}.
 * <br>
 */
public class FileSystemPolicySetDAO implements PolicySetDAO {

    private static final Logger log = LoggerFactory.getLogger(FileSystemPolicySetDAO.class);

    /** Cache of the policy sets */
    private static final Map<String, Map<String, PolicySetTypeString>> cache = new ConcurrentHashMap<String, Map<String, PolicySetTypeString>>();

    private static final PolicySetHelper policySetHelper = PolicySetHelper.getInstance();
    private static FileSystemPolicySetDAO instance = null;

    private FileSystemPolicySetDAO() {}

    public static FileSystemPolicySetDAO getInstance() {
        if (instance == null) {
            instance = new FileSystemPolicySetDAO();
        }
        return instance;
    }

    /**
     * Returns the absolute pathname string of the policy set file.
     * 
     * @param papId pap id containing the policy.
     * @param policySetId the policy id.
     * @return the absolute pathname string.
     */
    private static String getPolicySetAbsolutePath(String papId, String policySetId) {
        return FileSystemRepositoryManager.getPAPDirAbsolutePath(papId) + getPolicySetFileName(policySetId);
    }

    /**
     * Returns the file name of the policy set from the policy id.
     * 
     * @param policySetId the policy set id.
     * @return the policy set file name (just the name, not the absolute path).
     */
    private static String getPolicySetFileName(String policySetId) {
        return FileSystemRepositoryManager.POLICYSET_FILENAME_PREFIX + policySetId
                + FileSystemRepositoryManager.XACML_FILENAME_EXTENSION;
    }

    /**
     * Returns the policy set id from the file name of the policy set.
     * 
     * @param fileName the file name.
     * @return the policy set id.
     */
    private static String getPolicySetIdFromFileName(String fileName) {
        int start = FileSystemRepositoryManager.POLICYSET_FILENAME_PREFIX.length();
        int end = fileName.length() - FileSystemRepositoryManager.XACML_FILENAME_EXTENSION.length();
        return fileName.substring(start, end);
    }

    private static String papDirNotFoundExceptionMsg(String papDirPAth) {
        return "Not found PAP directory: " + papDirPAth;
    }

    private static String policySetExceptionMsg(String policySetId) {
        return String.format("policySetId=\"%s\"", policySetId);
    }

    private static String policySetNotFoundExceptionMsg(String policySetId) {
        String msg = "Not found: " + policySetExceptionMsg(policySetId);
        return msg;
    }

    /**
     * {@Inherited}
     */
    public synchronized void delete(String papId, String policySetId) {

        Map<String, PolicySetTypeString> papCache = cache.get(papId);

        if (papCache != null) {
            papCache.remove(policySetId);
        }

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));

        if (policySetFile.exists()) {

            if (papCache.size() == 0) {
                cache.remove(papId);
            }

            if (!policySetFile.delete()) {
                throw new RepositoryException("Cannot delete file: " + policySetFile.getAbsolutePath());
            }

        } else {
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
        }
    }

    /**
     * {@Inherited}
     */
    public synchronized void deleteAll(String papId) {

        Map<String, PolicySetTypeString> papCache = cache.get(papId);

        if (papCache != null) {
            papCache.clear();
            cache.remove(papId);
        }

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists()) {
            throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));
        }

        for (File file : papDir.listFiles()) {

            if (file.isDirectory()) {
                continue;
            }

            String fileName = file.getName();
            if (fileName.startsWith(FileSystemRepositoryManager.POLICYSET_FILENAME_PREFIX)) {
                file.delete();
            }
        }
    }

    /**
     * {@Inherited}
     */
    public synchronized boolean exists(String papId, String policySetId) {

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));

        boolean result = policySetFile.exists();

        log.debug(String.format("PolicySet \"%s\" exists=%s (file=\"%s\")",
                                policySetId,
                                String.valueOf(result),
                                policySetFile.getAbsoluteFile()));
        return result;
    }

    /**
     * {@Inherited}
     */
    public synchronized List<PolicySetType> getAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists()) {
            throw new NotFoundException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));
        }

        Map<String, PolicySetTypeString> papCache = getPapCache(papId);

        List<PolicySetType> policySetList = new LinkedList<PolicySetType>();

        for (File file : papDir.listFiles()) {

            if (file.isDirectory()) {
                continue;
            }

            String fileName = file.getName();

            if (fileName.startsWith(FileSystemRepositoryManager.POLICYSET_FILENAME_PREFIX)) {

                String policySetId = getPolicySetIdFromFileName(fileName);

                PolicySetTypeString policySet = papCache.get(policySetId);

                if (policySet == null) {
                    try {
                        policySet = new PolicySetTypeString(policySetId,
                                                            policySetHelper.readFromFileAsString(file));
                        log.debug("getAll(): PolicySet retrieved from file: id=" + policySetId);
                    } catch (Throwable e) {
                        throw new RepositoryException(e);
                    }

                    papCache.put(policySetId, policySet);
                } else {
                    log.debug("getAll(): PolicySet retrieved from cache: id=" + policySetId);
                }

                policySetList.add(new PolicySetTypeString(policySetId, policySet.getPolicySetString()));

                if (policySet.isDOMLoaded()) {
                    log.warn("getAll(): DOM not released for PolicySet id=" + policySetId);
                }
            }
        }
        return policySetList;
    }

    /**
     * {@Inherited}
     */
    public synchronized PolicySetType getById(String papId, String policySetId) {

        Map<String, PolicySetTypeString> papCache = getPapCache(papId);

        PolicySetTypeString policySet = papCache.get(policySetId);

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));
        if (policySet == null) {

            if (!policySetFile.exists()) {
                if (papCache.size() == 0) {
                    cache.remove(papId);
                }
                throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
            }

            try {
                policySet = new PolicySetTypeString(policySetId,
                                                    policySetHelper.readFromFileAsString(policySetFile));
            } catch (Throwable e) {
                throw new RepositoryException(e);
            }

            papCache.put(policySetId, policySet);
            log.debug("getById(): PolicySet retrieved from file: id=" + policySetId);

        } else {
            log.debug("getById(): PolicySet retrieved from cache: id=" + policySetId);
        }

        if (policySet.isDOMLoaded()) {
            log.warn("getById(): DOM not released for PolicySet id=" + policySetId);
        }

        return new PolicySetTypeString(policySetId, policySet.getPolicySetString());
    }

    /**
     * {@Inherited}
     */
    public synchronized void store(String papId, PolicySetType policySet) {

        PolicySetTypeString policySetTypeString = TypeStringUtils.cloneAsPolicySetTypeString(policySet);

        TypeStringUtils.releaseUnneededMemory(policySet);

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists())
            throw new NotFoundException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

        String policySetId = policySetTypeString.getPolicySetId();

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));
        if (policySetFile.exists()) {
            throw new AlreadyExistsException("Already exists: policySetId=" + policySetId);
        }

        PolicySetHelper.toFile(policySetFile, policySetTypeString);

        TypeStringUtils.releaseUnneededMemory(policySetTypeString);

        getPapCache(papId).put(policySetId, policySetTypeString);
    }

    public synchronized void update(String papId, String policySetVersion, PolicySetType newPolicySet) {

        String policySetId = newPolicySet.getPolicySetId();

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));
        if (!policySetFile.exists()) {
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
        }

        Map<String, PolicySetTypeString> papCache = getPapCache(papId);
        PolicySetTypeString oldPolicySetString = papCache.get(policySetId);

        if (oldPolicySetString == null) {
            try {
                oldPolicySetString = new PolicySetTypeString(policySetHelper.readFromFileAsString(policySetFile));
                log.debug("update(): PolicySet retrieved from file: id=" + policySetId);
            } catch (Throwable e) {
                throw new RepositoryException(e);
            }
        } else {
            log.debug("update(): PolicySet retrieved from cache: id=" + policySetId);
        }

        PolicySetTypeString oldPolicySet = new PolicySetTypeString(oldPolicySetString);

        if (!(oldPolicySet.getVersion().equals(policySetVersion))) {
            throw new InvalidVersionException(String.format("Attempting to update the wrong version of PolicySetId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                                                            policySetId,
                                                            policySetVersion,
                                                            oldPolicySet.getVersion()));
        }

        TypeStringUtils.releaseUnneededMemory(oldPolicySetString);

        PolicySetHelper.toFile(policySetFile, newPolicySet);

        PolicySetTypeString newPolicySetTypeString = TypeStringUtils.cloneAsPolicySetTypeString(newPolicySet);

        papCache.put(policySetId, newPolicySetTypeString);
    }

    /**
     * Returns the cached policy sets of a pap.
     * 
     * @param papId <code>id</code> of the pap.
     * @return {@code Map<String, PolicySetTypeString>} where the <code>key</code> is the
     *         policySetId and the <code>value</code> is the policy set.
     */
    private Map<String, PolicySetTypeString> getPapCache(String papId) {
        Map<String, PolicySetTypeString> papCache = cache.get(papId);

        if (papCache == null) {
            log.debug("New HashMap for papId=" + papId);
            papCache = new ConcurrentHashMap<String, PolicySetTypeString>();
            cache.put(papId, papCache);
        }
        return papCache;
    }
}
