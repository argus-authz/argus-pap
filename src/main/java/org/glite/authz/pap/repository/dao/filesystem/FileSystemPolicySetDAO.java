package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemPolicySetDAO implements PolicySetDAO {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(FileSystemPolicySetDAO.class);
    private static final PolicySetHelper policySetHelper = PolicySetHelper.getInstance();
    private static final String policySetFileNamePrefix = FileSystemRepositoryManager
            .getPolicySetFileNamePrefix();

    public static FileSystemPolicySetDAO getInstance() {
        return new FileSystemPolicySetDAO();
    }

    private FileSystemPolicySetDAO() {}

    public void delete(String papId, String policySetId) {

        String policySetFileName = FileSystemRepositoryManager.getPolicySetAbsolutePath(papId,
                policySetId);

        if (exists(papId, policySetId)) {
            File policySetFile = new File(policySetFileName);

            if (!policySetFile.delete())
                throw new RepositoryException("Cannot delete PolicySet \"" + policySetId + "\": "
                        + policySetFileName);

        } else
            throw new NotFoundException("Not found: (papId=" + papId + ", PolicySetId=" + policySetId
                    + "). Resource: " + policySetFileName);
    }

    public void deleteAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        if (!papDir.exists())
            throw new NotFoundException("Not found: papId=" + papId);

        for (File file : papDir.listFiles()) {

            if (file.isDirectory())
                continue;

            if (file.getName().startsWith(policySetFileNamePrefix))
                file.delete();
        }
    }

    public boolean exists(String papId, String policySetId) {
        File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId,
                policySetId));
        return policySetFile.exists();
    }

    public List<PolicySetType> getAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        if (!papDir.exists())
            throw new NotFoundException("Not found: papId=" + papId);

        List<PolicySetType> policySetList = new LinkedList<PolicySetType>();

        for (File file : papDir.listFiles()) {

            if (file.isDirectory())
                continue;

            if (file.getName().startsWith(policySetFileNamePrefix))
                policySetList.add(policySetHelper.buildFromFile(file));
        }

        return policySetList;
    }

    public PolicySetType getById(String papId, String policySetId) {

        File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId,
                policySetId));

        if (exists(papId, policySetId)) {

            if (!policySetFile.exists())
                throw new NotFoundException("PolicySet not found: id=" + policySetId);

        } else
            throw new NotFoundException("Not found: (papId=" + papId + ", PolicySetId=" + policySetId
                    + "). Resource: " + policySetFile.getAbsolutePath());

        return policySetHelper.buildFromFile(policySetFile);
    }

    public void store(String papId, PolicySetType ps) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        if (!papDir.exists())
            throw new NotFoundException("Not found: papId=" + papId);

        if (exists(papId, ps.getPolicySetId()))
            throw new AlreadyExistsException("PolicySet already exists: id=" + ps.getPolicySetId());

        PolicySetHelper.toFile(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, ps
                .getPolicySetId()), ps);
    }

    public void update(String papId, PolicySetType ps) {

        File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, ps
                .getPolicySetId()));

        if (!exists(papId, ps.getPolicySetId()))
            throw new NotFoundException("Not found: (papId=" + papId + ", PolicySetId="
                    + ps.getPolicySetId() + "). Resource: " + policySetFile.getAbsolutePath());

        PolicySetHelper.toFile(policySetFile, ps);
    }

}
