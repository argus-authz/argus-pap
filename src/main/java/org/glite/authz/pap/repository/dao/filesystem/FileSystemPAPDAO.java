package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.dao.PAPDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPAPDAO implements PAPDAO {
    
    private static String dbPath = FileSystemRepositoryManager.getFileSystemDatabaseDir();
    
    private FileSystemPAPDAO() { }
    
    public static FileSystemPAPDAO getInstance() {
        return new FileSystemPAPDAO();
    }

    public void add(PAP pap) {
        
        if (exists(pap.getPapId()))
            throw new AlreadyExistsException("Already exists: papId=" + pap.getPapId());
        
        File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
        if (!directory.mkdir())
            throw new RepositoryException("Cannot create directory for PAP: " + pap.getPapId());
    }

    public void delete(String papId) {
        
        if (!exists(papId))
            throw new NotFoundException("Not found: papId=" + papId);
        
        File papDir = new File(getPAPDirAbsolutePath(papId));
        for (File file : papDir.listFiles()) {
            file.delete();
        }
        papDir.delete();
    }

    public boolean exists(String papId) {
        File directory = new File(getPAPDirAbsolutePath(papId));
        return directory.exists();
    }

    public List<PAP> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    public PAP getById(String papId) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setOrder(List<String> papId) {
    // TODO Auto-generated method stub

    }

    public void update(PAP pap) {
    // TODO Auto-generated method stub

    }
    
    private String getPAPDirAbsolutePath(String papId) {
        return dbPath + File.separator + papId;
    }

}
