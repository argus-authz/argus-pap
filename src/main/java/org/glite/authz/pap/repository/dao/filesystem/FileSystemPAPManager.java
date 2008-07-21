package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.PAPManager;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPAPManager implements PAPManager {

    public static FileSystemPAPManager getInstance() {
	return new FileSystemPAPManager();
    }

    private final String dbDir;

    private FileSystemPAPManager() {
	dbDir = FileSystemRepositoryManager.getFileSystemDatabaseDir();
    }

    public PAPContainer create(PAP pap) {
	if (exists(pap)) {
	    throw new AlreadyExistsException();
	}
	File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
	if (!directory.mkdir()) {
	    throw new RepositoryException("Cannot create directory for PAP: "
		    + pap.getPapId());
	}
	return new PAPContainer(pap);
    }

    public void delete(PAP pap) {
	File papDir = new File(getPAPDirAbsolutePath(pap.getPapId()));
	for (File file : papDir.listFiles()) {
	    file.delete();
	}
	papDir.delete();
    }

    public boolean exists(PAP pap) {
	File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
	return directory.exists();
    }

    public PAPContainer get(PAP pap) {
	if (!exists(pap)) {
	    throw new NotFoundException();
	}
	return new PAPContainer(pap);
    }

    public List<PAPContainer> getAll() {
	File dir = new File(dbDir);
	File[] list = dir.listFiles();
	List<PAPContainer> idList = new ArrayList<PAPContainer>(list.length);
	for (File file : list) {
	    if (file.isDirectory()) {
		idList.add(new PAPContainer(new PAP(file.getName())));
	    }
	}
	return idList;
    }

    private String getPAPDirAbsolutePath(String papId) {
	return dbDir + File.separator + papId;
    }
}
