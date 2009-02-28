package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemPolicySetDAO implements PolicySetDAO {

    private static final Logger log = LoggerFactory.getLogger(FileSystemPolicySetDAO.class);
    private static final String policySetFileNamePrefix = FileSystemRepositoryManager.getPolicySetFileNamePrefix();
    private static final PolicySetHelper policySetHelper = PolicySetHelper.getInstance();

    // TODO: maybe it's better to create different exception classes instead of
    // different exception messages
    private static String policySetExceptionMsg(String policySetId) {
        return String.format("policySetId=\"%s\"", policySetId);
    }

    private static String papDirNotFoundExceptionMsg(String papDirPAth) {
        return "Not found PAP directory: " + papDirPAth;
    }

    private static String policySetNotFoundExceptionMsg(String policySetId) {
        String msg = "Not found: " + policySetExceptionMsg(policySetId);
        return msg;
    }

    private FileSystemPolicySetDAO() {}

    public static FileSystemPolicySetDAO getInstance() {
        return new FileSystemPolicySetDAO();
    }

    public void delete(String papId, String policySetId) throws NotFoundException, RepositoryException {

        String policySetFileName = FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId);

        if (exists(papId, policySetId)) {
            File policySetFile = new File(policySetFileName);

            if (!policySetFile.delete())
                throw new RepositoryException("Cannot delete file: " + policySetFile.getAbsolutePath());

        } else
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
    }

    public void deleteAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists())
            throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

        for (File file : papDir.listFiles()) {

            if (file.isDirectory())
                continue;

            if (file.getName().startsWith(policySetFileNamePrefix))
                file.delete();
        }
    }

    public boolean exists(String papId, String policySetId) {

        File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId));

        boolean result = policySetFile.exists();
        
        log.debug(String.format("PolicySet \"%s\" exists=%s (file=\"%s\")", policySetId, String.valueOf(result), policySetFile.getAbsoluteFile()));
        
        return result;
    }

    public List<PolicySetType> getAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists())
            throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

        List<PolicySetType> policySetList = new LinkedList<PolicySetType>();

        for (File file : papDir.listFiles()) {

            if (file.isDirectory())
                continue;

            if (file.getName().startsWith(policySetFileNamePrefix))
                policySetList.add(policySetHelper.buildFromFile(file));
        }

        return policySetList;
    }

    public PolicySetType getById(String papId, String policySetId) throws NotFoundException, RepositoryException {

        File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId));

        if (!exists(papId, policySetId)) {
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
        }

        PolicySetType policySet;
        try {
            policySet = policySetHelper.buildFromFile(policySetFile);
        } catch (Throwable e) {
            throw new RepositoryException(e);
        }

        return policySet;
    }

    public void store(String papId, PolicySetType ps) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));

        if (!papDir.exists())
            throw new RepositoryException(papDirNotFoundExceptionMsg(papDir.getAbsolutePath()));

        String policySetId = ps.getPolicySetId();

        if (exists(papId, policySetId))
            throw new AlreadyExistsException("Already exists: policySetId=" + policySetId);

        PolicySetHelper.toFile(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId), ps);
    }

    public void update(String papId, PolicySetType ps) {

        String policySetId = ps.getPolicySetId();

        File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId));

        if (!exists(papId, policySetId))
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));

        PolicySetHelper.toFile(policySetFile, ps);
    }

}
