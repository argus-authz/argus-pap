package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.glite.authz.pap.common.xacml.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemPolicySetDAO implements PolicySetDAO {

    private static final Map<String, Map<String, String>> cache = new ConcurrentHashMap<String, Map<String, String>>();
    private static final String FILE_EXT = FileSystemRepositoryManager.getFileNameExt();
    private static final Logger log = LoggerFactory.getLogger(FileSystemPolicySetDAO.class);
    private static final String POLICY_SET_FILE_NAME_PREFIX = FileSystemRepositoryManager.getPolicySetFileNamePrefix();
    private static final PolicySetHelper policySetHelper = PolicySetHelper.getInstance();
    private static FileSystemPolicySetDAO instance = null;

    private FileSystemPolicySetDAO() {}

    public static FileSystemPolicySetDAO getInstance() {
        if (instance == null) {
            instance = new FileSystemPolicySetDAO();
        }
        return instance;
    }

    private static String getPolicySetAbsolutePath(String papId, String policySetId) {
        return FileSystemRepositoryManager.getPAPDirAbsolutePath(papId) + getPolicySetFileName(policySetId);
    }

    private static String getPolicySetFileName(String policySetId) {
        return POLICY_SET_FILE_NAME_PREFIX + policySetId + FILE_EXT;
    }

    private static String getPolicySetIdFromFileName(String fileName) {
        int start = POLICY_SET_FILE_NAME_PREFIX.length();
        int end = fileName.length() - FILE_EXT.length();
        return fileName.substring(start, end);
    }

    private static String papDirNotFoundExceptionMsg(String papDirPAth) {
        return "Not found PAP directory: " + papDirPAth;
    }

    // TODO: maybe it's better to create different exception classes instead of
    // different exception messages
    private static String policySetExceptionMsg(String policySetId) {
        return String.format("policySetId=\"%s\"", policySetId);
    }

    private static String policySetNotFoundExceptionMsg(String policySetId) {
        String msg = "Not found: " + policySetExceptionMsg(policySetId);
        return msg;
    }

    public synchronized void delete(String papId, String policySetId) {

        Map<String, String> papCache = cache.get(papId);

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

    public synchronized void deleteAll(String papId) {

        Map<String, String> papCache = cache.get(papId);

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
            if (fileName.startsWith(POLICY_SET_FILE_NAME_PREFIX)) {
                file.delete();
            }
        }
    }

    public boolean exists(String papId, String policySetId) {

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));

        boolean result = policySetFile.exists();

        log.debug(String.format("PolicySet \"%s\" exists=%s (file=\"%s\")",
                                policySetId,
                                String.valueOf(result),
                                policySetFile.getAbsoluteFile()));
        return result;
    }

    public List<PolicySetType> getAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists()) {
            throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));
        }

        Map<String, String> papCache = getPAPCache(papId);

        List<PolicySetType> policySetList = new LinkedList<PolicySetType>();

        for (File file : papDir.listFiles()) {

            if (file.isDirectory()) {
                continue;
            }

            String fileName = file.getName();

            if (fileName.startsWith(POLICY_SET_FILE_NAME_PREFIX)) {

                String policySetId = getPolicySetIdFromFileName(fileName);
                String policySetString = papCache.get(policySetId);

                if (policySetString == null) {
                    try {
                        policySetString = policySetHelper.readFromFileAsString(file);
                    } catch (Throwable e) {
                        throw new RepositoryException(e);
                    }
                    papCache.put(policySetId, policySetString);
                }
                policySetList.add(new PolicySetTypeString(policySetString));
            }
        }
        return policySetList;
    }

    public PolicySetType getById(String papId, String policySetId) {

        Map<String, String> papCache = getPAPCache(papId);

        String policySetString = papCache.get(policySetId);

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));
        if (policySetString == null) {

            if (!policySetFile.exists()) {
                if (papCache.size() == 0) {
                    cache.remove(papId);
                }
                throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
            }

            try {
                policySetString = policySetHelper.readFromFileAsString(policySetFile);
            } catch (Throwable e) {
                throw new RepositoryException(e);
            }

            papCache.put(policySetId, policySetString);
            log.debug("getById(): PolicySet retrieved from file: id=" + policySetId);

        } else {
            log.debug("getById(): PolicySet retrieved from cache: id=" + policySetId);
        }

        return new PolicySetTypeString(policySetString);
    }

    public synchronized void store(String papId, PolicySetType policySet) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists())
            throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

        String policySetId = policySet.getPolicySetId();

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));
        if (policySetFile.exists()) {
            throw new AlreadyExistsException("Already exists: policySetId=" + policySetId);
        }

        PolicySetHelper.toFile(policySetFile, policySet);

        Map<String, String> papCache = getPAPCache(papId);
        papCache.put(policySetId, PolicySetHelper.toString(policySet));
    }

    public synchronized void update(String papId, String policySetVersion, PolicySetType newPolicySet) {

        String policySetId = newPolicySet.getPolicySetId();

        File policySetFile = new File(getPolicySetAbsolutePath(papId, policySetId));
        if (!policySetFile.exists()) {
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
        }

        Map<String, String> papCache = getPAPCache(papId);
        String oldPolicySetString = papCache.get(policySetId);

        if (oldPolicySetString == null) {
            try {
                oldPolicySetString = policySetHelper.readFromFileAsString(policySetFile);
                log.debug("update(): PolicySet retrieved from file: id=" + policySetId);
            } catch (Throwable e) {
                throw new RepositoryException(e);
            }
        } else {
            log.debug("update(): PolicySet retrieved from cache: id=" + policySetId);
        }

        PolicySetTypeString oldPolicySet = new PolicySetTypeString(oldPolicySetString);

        if (!(oldPolicySet.getVersion().equals(policySetVersion))) {
            throw new RepositoryException(
                String.format("Attempting to update the wrong version of PolicySetId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                              policySetId,
                              policySetVersion,
                              oldPolicySet.getVersion()));
        }

        PolicySetHelper.toFile(policySetFile, newPolicySet);

        papCache.put(policySetId, PolicySetHelper.toString(newPolicySet));
    }

    private Map<String, String> getPAPCache(String papId) {
        if (!papId.equals("Local"))
            throw new RuntimeException("proca troia");
        Map<String, String> papCache = cache.get(papId);

        if (papCache == null) {
            log.debug("New HashMap for papId=" + papId);
            papCache = new ConcurrentHashMap<String, String>();
            cache.put(papId, papCache);
        }
        return papCache;
    }
}
