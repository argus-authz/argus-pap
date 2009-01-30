package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.INIConfiguration;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.dao.PAPDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPAPDAO implements PAPDAO {
    
	private static String PAP_FILE_NAME = "pap_info.ini";
	private static String REMOTE_PAP_STANZA = "remote-pap";
    private static String dbPath = FileSystemRepositoryManager.getFileSystemDatabaseDir();
    private INIConfiguration remotePAPsInfo;
    
    private static String aliasKey(String papAlias) {
        return REMOTE_PAP_STANZA + "." + papAlias;
    }
    
    private static String hostnameKey(String papAlias) {
    	return aliasKey(papAlias) + "." + "hostname";
    }
    
    private static String portKey(String papAlias) {
    	return aliasKey(papAlias) + "." + "port";
    }
    
    private static String pathKey(String papAlias) {
    	return aliasKey(papAlias) + "." + "path";
    }
    
    private static String dnKey(String papAlias) {
    	return aliasKey(papAlias) + "." + "dn";
    }
    
    private static String protocolKey(String papAlias) {
    	return aliasKey(papAlias) + "." + "protocol";
    }

    private static String idKey(String papAlias) {
    	return aliasKey(papAlias) + "." + "id";
    }
    
    public static FileSystemPAPDAO getInstance() {
        return new FileSystemPAPDAO();
    }
    
    private FileSystemPAPDAO() {
    	remotePAPsInfo = new INIConfiguration();
    	
    	File remotePAPsFile = new File(dbPath + File.separator + PAP_FILE_NAME);
    	
    	remotePAPsInfo.setFile(remotePAPsFile);
    	
    	try {
    		
    		if (!remotePAPsFile.exists())
    			remotePAPsInfo.save();
    		
			remotePAPsInfo.load();
		} catch (ConfigurationException e) {
			throw new RepositoryException("Error during initialization", e);
		}
    	
    }

    public void add(PAP pap) {
        
        if (exists(pap.getPapId()))
            throw new AlreadyExistsException("Already exists: papId=" + pap.getPapId());
        
        File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
        if (!directory.mkdir())
            throw new RepositoryException("Cannot create directory for PAP: " + pap.getPapId());

        setPAPProperties(pap);
        try {
        	remotePAPsInfo.save();
		} catch (ConfigurationException e) {
			throw new RepositoryException(e);
		}
    }

    public void delete(String papId) {
        
        if (!exists(papId))
            throw new NotFoundException("Not found: papId=" + papId);
        
        File papDir = new File(getPAPDirAbsolutePath(papId));
        for (File file : papDir.listFiles()) {
            file.delete();
        }
        papDir.delete();
        
        clearPAPProperties(papId);
        try {
			remotePAPsInfo.save();
		} catch (ConfigurationException e) {
			throw new RepositoryException(e);
		}
    }

    public boolean exists(String papId) {
        File directory = new File(getPAPDirAbsolutePath(papId));
        return directory.exists();
    }

    public List<PAP> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getAllIds() {
        List<String> idList = new LinkedList<String>();
        
        File dbDir = new File(dbPath);
        
        for (File file:dbDir.listFiles()) {
            
            if (file.isDirectory()) {
                String dirName = file.getName();
                if (PAP.localPAPId.equals(dirName))
                    continue;
                idList.add(dirName);
            }
        }
        return idList;
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
    
    private void setPAPProperties(PAP pap) {
    	String papAlias = pap.getAlias();
    	remotePAPsInfo.setProperty(dnKey(papAlias), pap.getDn());
    	remotePAPsInfo.setProperty(hostnameKey(papAlias), pap.getHostname());
    	remotePAPsInfo.setProperty(portKey(papAlias), pap.getPort());
    	remotePAPsInfo.setProperty(pathKey(papAlias), pap.getPath());
    	remotePAPsInfo.setProperty(protocolKey(papAlias), pap.getProtocol());
    	remotePAPsInfo.setProperty(idKey(papAlias), pap.getPapId());
    }
    
    private void clearPAPProperties(String papAlias) {
    	remotePAPsInfo.clearProperty(dnKey(papAlias));
    	remotePAPsInfo.clearProperty(hostnameKey(papAlias));
    	remotePAPsInfo.clearProperty(portKey(papAlias));
    	remotePAPsInfo.clearProperty(pathKey(papAlias));
    	remotePAPsInfo.clearProperty(protocolKey(papAlias));
    	remotePAPsInfo.clearProperty(idKey(papAlias));
    }

}
