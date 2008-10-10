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
    private static final PolicyHelper policyHelper = PolicyHelper.getInstance();
    private static final String policyFileNamePrefix = FileSystemRepositoryManager
            .getPolicyFileNamePrefix();

    public static FileSystemPolicyDAO getInstance() {
        return new FileSystemPolicyDAO();
    }

    private FileSystemPolicyDAO() {}

    public void delete(String papId, String policyId) throws NotFoundException, RepositoryException {
        String policyFileName = FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId);

        if (exists(papId, policyId)) {

            File policyFile = new File(policyFileName);

            if (!policyFile.delete())
                throw new RepositoryException("Cannot delete Policy \"" + policyId + "\": "
                        + policyFileName);

        } else
            throw new NotFoundException("Not found: (papId=" + papId + ", PolicyId=" + policyId
                    + "). Resource: " + policyFileName);
    }

    public void deleteAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        if (!papDir.exists())
            throw new NotFoundException("Not found: papId=" + papId);

        for (File file : papDir.listFiles()) {

            if (file.isDirectory())
                continue;

            if (file.getName().startsWith(policyFileNamePrefix)) {
                file.delete();
            }
        }
    }

    public boolean exists(String papId, String policyId) {
        File policyFile = new File(FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId));
        return policyFile.exists();
    }

    public List<PolicyType> getAll(String papId) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        if (!papDir.exists())
            throw new NotFoundException("Not found: papId=" + papId);

        List<PolicyType> policyList = new LinkedList<PolicyType>();

        for (File file : papDir.listFiles()) {

            if (file.isDirectory())
                continue;

            if (file.getName().startsWith(policyFileNamePrefix))
                policyList.add(policyHelper.buildFromFile(file));

        }

        return policyList;
    }

    public PolicyType getById(String papId, String policyId) {

        File policyFile = new File(FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policyId));

        if (exists(papId, policyId)) {

            if (!policyFile.exists())
                throw new RepositoryException("Policy does not exist: " + policyId);

        } else
            throw new NotFoundException("Not found: (papId=" + papId + ", PolicyId=" + policyId
                    + "). Resource: " + policyFile.getAbsolutePath());

        return policyHelper.buildFromFile(policyFile);
    }

    public void store(String papId, PolicyType policy) {

        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        if (!papDir.exists())
            throw new NotFoundException("Not found: papId=" + papId);

        if (exists(papId, policy.getPolicyId()))
            throw new AlreadyExistsException("Policy already exists: id=" + policy.getPolicyId());

        PolicyHelper.toFile(FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policy
                .getPolicyId()), policy);
    }

    public void update(String papId, PolicyType policy) {

        File policyFile = new File(FileSystemRepositoryManager.getPolicyAbsolutePath(papId, policy
                .getPolicyId()));

        if (!exists(papId, policy.getPolicyId()))
            throw new NotFoundException("Not found: (papId=" + papId + ", PolicyId="
                    + policy.getPolicyId() + "). Resource: " + policyFile.getAbsolutePath());

        PolicyHelper.toFile(policyFile, policy);
    }
}
