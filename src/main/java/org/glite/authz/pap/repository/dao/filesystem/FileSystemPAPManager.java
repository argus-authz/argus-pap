package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPAPManager extends PAPManager {
    
    private final String dbDir;

    public FileSystemPAPManager() {
        dbDir = FileSystemRepositoryManager.getFileSystemDatabaseDir();
    }

    @Override
    public PAPContainer add(PAP pap) {
        if (exists(pap.getPapId())) {
            throw new AlreadyExistsException();
        }
        File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
        if (!directory.mkdir()) {
            throw new RepositoryException("Cannot create directory for PAP: " + pap.getPapId());
        }
        papList.add(pap);
        return new PAPContainer(pap);
    }

    @Override
    public PAP delete(String papId) {
        if (!exists(papId))
            throw new NotFoundException("PAP id not found: " + papId);
        File papDir = new File(getPAPDirAbsolutePath(papId));
        for (File file : papDir.listFiles()) {
            file.delete();
        }
        papDir.delete();
        for (PAP papElem:papList) {
            if (papElem.getPapId().equals(papId)) {
                papList.remove(papElem);
                return papElem;
            }
        }
        throw new NotFoundException("BUG! PAP id not found: " + papId);
    }

    @Override
    public boolean exists(String papId) {
        // TODO: use papList
        File directory = new File(getPAPDirAbsolutePath(papId));
        return directory.exists();
    }

    @Override
    public PAP get(String papId) {
        for (PAP pap:papList) {
            if (pap.getPapId().equals(papId)) {
                return pap;
            }
        }
        throw new NotFoundException("PAP not found: " + papId);
    }

    @Override
    public List<PAP> getAll() {
        return papList;
    }

    @Override
    public PAPContainer getContainer(String papId) {
        return new PAPContainer(get(papId));
    }

    @Override
    public List<PAPContainer> getContainerAll() {
        List<PAPContainer> papContainerList = new ArrayList<PAPContainer>(papList.size());
        for (PAP pap:papList) {
            papContainerList.add(new PAPContainer(pap));
        }
        return papContainerList;
//        File dir = new File(dbDir);
//        File[] list = dir.listFiles();
//        for (File file : list) {
//            if (file.isDirectory()) {
//                papContainerList.add(new PAPContainer(new PAP(file.getName())));
//            }
//        }
    }

    @Override
    public void setPAPOrder(List<String> papIdList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(String papId, PAP newpap) {
        for (int i=0; i<papList.size(); i++) {
            PAP pap = papList.get(i);
            if (pap.getPapId().equals(papId)) {
                papList.set(i, newpap);
                break;
            }
        }
    }

    private String getPAPDirAbsolutePath(String papId) {
        return dbDir + File.separator + papId;
    }
}
