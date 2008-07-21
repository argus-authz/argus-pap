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

    private static final Logger log = LoggerFactory.getLogger(FileSystemPolicySetDAO.class);

    public static FileSystemPolicySetDAO getInstance() {
        return new FileSystemPolicySetDAO();
    }

    private static final String policySetFileNamePrefix;
    private static final PolicySetHelper policySetHelper;
    
    static {
        policySetFileNamePrefix = FileSystemRepositoryManager.getPolicySetFileNamePrefix();
        policySetHelper = PolicySetHelper.getInstance();
    }

    private FileSystemPolicySetDAO() {}

    public void delete(String papId, String policySetId) {
        
        checkPAPExists(papId);
        
        String policySetFilePath = FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId);
        
        if (!exists(papId, policySetId))
            throw new RepositoryException("PolicySet \"" + policySetId + "\" does not exists: " + policySetFilePath);
        
        File policySetFile = new File(policySetFilePath);
        
        if (!policySetFile.delete()) {
            throw new RepositoryException("Cannot delete PolicySet \"" + policySetId + "\": " + policySetFilePath);
        }
    }

    public void deleteAll(String papId) {
        
        checkPAPExists(papId);
        
        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        
        for (File file : papDir.listFiles()) {
            if (file.isDirectory())
                continue;
            if (file.getName().startsWith(policySetFileNamePrefix))
                file.delete();
        }
    }

    public boolean exists(String papId, String policySetId) {
        return new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId))
                .exists();
    }

    public List<PolicySetType> getAll(String papId) {
        
        checkPAPExists(papId);
        
        List<PolicySetType> policySetList = new LinkedList<PolicySetType>();
        
        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        
        for (File file : papDir.listFiles()) {
            if (file.isDirectory())
                continue;
            if (file.getName().startsWith(policySetFileNamePrefix))
                policySetList.add(policySetHelper.buildFromFile(file));
        }
        return policySetList;
    }

    public PolicySetType getById(String papId, String policySetId) {
        
        checkPAPExists(papId);
        
        String policySetFileName = FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, policySetId);
        
        if (!exists(papId, policySetId))
            throw new NotFoundException("PolicySet \"" + policySetId + "\" not found: " + policySetFileName);
        
        File policySetFile = new File(policySetFileName);
        
        if (!policySetFile.exists()) {
            throw new RepositoryException("PolicySet does not exist: " + policySetId);
        }
        
        return policySetHelper.buildFromFile(policySetFile);
    }

    public void store(String papId, PolicySetType ps) {
        
        checkPAPExists(papId);
        
        if (exists(papId, ps.getPolicySetId()))
            throw new AlreadyExistsException("PolicySe already exists: " + ps.getPolicySetId());

        policySetHelper.toFile(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, ps
                .getPolicySetId()), ps);
    }

    public void update(String papId, PolicySetType ps) {
        
        checkPAPExists(papId);
        
        if (!exists(papId, ps.getPolicySetId()))
            throw new RepositoryException("PolicySet does not exist: " + ps.getPolicySetId());
            
        File policySetFile = new File(FileSystemRepositoryManager.getPolicySetAbsolutePath(papId, ps
                .getPolicySetId()));
        
        policySetHelper.toFile(policySetFile, ps);
    }
    
    private void checkPAPExists(String papId) {
        File papDir = new File(FileSystemRepositoryManager.getPAPDirAbsolutePath(papId));
        if (!papDir.exists())
            throw new RepositoryException("PAP does not exists: " + papId);
    }
    
}
