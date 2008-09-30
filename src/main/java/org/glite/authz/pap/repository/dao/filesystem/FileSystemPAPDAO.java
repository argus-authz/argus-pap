package org.glite.authz.pap.repository.dao.filesystem;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.dao.PAPDAO;

public class FileSystemPAPDAO implements PAPDAO {
    
    private static String dbPAth = FileSystemRepositoryManager.getFileSystemDatabaseDir();

    public void add(PAP pap) {
    // TODO Auto-generated method stub

    }

    public void add(PAP pap, int index) {
    // TODO Auto-generated method stub

    }

    public void delete(String papId) {
    // TODO Auto-generated method stub

    }

    public void deleteAll() {
    // TODO Auto-generated method stub

    }

    public boolean exists(String papId) {
        // TODO Auto-generated method stub
        return false;
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

}
