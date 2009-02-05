package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.INIConfiguration;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.repository.dao.PAPDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.mortbay.log.Log;

public class FileSystemPAPDAO implements PAPDAO {

    private static String PAP_FILE_NAME = "pap_info.ini";
    private static String REMOTE_PAP_STANZA = "remote-paps";
    private static FileSystemPAPDAO instance = null;
    private static String dbPath = FileSystemRepositoryManager.getFileSystemDatabaseDir();

    public static FileSystemPAPDAO getInstance() {
    	if (instance == null)
            instance = new FileSystemPAPDAO();
    	return instance;
    }

    private static String aliasKey(String papAlias) {
        return REMOTE_PAP_STANZA + "." + papAlias;
    }

    private static String dnKey(String papAlias) {
        return aliasKey(papAlias) + "." + "dn";
    }

    private static String hostnameKey(String papAlias) {
        return aliasKey(papAlias) + "." + "hostname";
    }

    private static String idKey(String papAlias) {
        return aliasKey(papAlias) + "." + "id";
    }

    private static String pathKey(String papAlias) {
        return aliasKey(papAlias) + "." + "path";
    }

    private static String portKey(String papAlias) {
        return aliasKey(papAlias) + "." + "port";
    }

    private static String protocolKey(String papAlias) {
        return aliasKey(papAlias) + "." + "protocol";
    }
    
    private static String visibilityPublicKey(String papAlias) {
        return aliasKey(papAlias) + "." + "public";
    }

    private INIConfiguration papsFile;

    private FileSystemPAPDAO() throws RepositoryException {
        papsFile = new INIConfiguration();

        File remotePAPsFile = new File(dbPath + File.separator + PAP_FILE_NAME);

        papsFile.setFile(remotePAPsFile);

        try {

            if (!remotePAPsFile.exists())
                papsFile.save();

            papsFile.load();
        } catch (ConfigurationException e) {
            throw new RepositoryException("Error during initialization", e);
        }

    }

    public void delete(String papAlias) throws NotFoundException, RepositoryException {

        if (!exists(papAlias))
            throw new NotFoundException("Not found: papAlias=" + papAlias);

        String papId = papsFile.getString(idKey(papAlias));
        
        File papDir = new File(getPAPDirAbsolutePath(papId));
        
        // empty PAP directory (delete all policies and policy sets)
        for (File file : papDir.listFiles()) {
            file.delete();
        }
        papDir.delete();

        // remove PAP from INI file
        clearPAPProperties(papAlias);
        try {
            papsFile.save();
        } catch (ConfigurationException e) {
            throw new RepositoryException(e);
        }
    }

    public List<PAP> getAll() {
        
        List<String> aliasList = getAllAliases();
        
        List<PAP> papList = new ArrayList<PAP>(aliasList.size());
        
        for (String alias:aliasList) {
            papList.add(getPAPFromProperies(alias));
        }

        return papList;
    }

    @SuppressWarnings("unchecked")
	public List<String> getAllAliases() {
    	
    	Set<String> aliasSet = new HashSet<String>();

        Iterator<String> iterator = papsFile.getKeys(REMOTE_PAP_STANZA);
        while (iterator.hasNext()) {
            String key = iterator.next();
            
            int firstAliasChar = key.indexOf('.') + 1;
            int lastAliasChar = key.indexOf('.', firstAliasChar);
            
            String alias = key.substring(firstAliasChar, lastAliasChar);
            
            aliasSet.add(alias);
        }
        
        List<String> aliasList = new ArrayList<String>();
        
        for (String alias : aliasSet) {
        	aliasList.add(alias);
        }

        return aliasList;
    }

    public PAP get(String papAlias) throws NotFoundException {
        
        PAP pap = getPAPFromProperies(papAlias);
        
        if (pap == null)
            throw new NotFoundException("PAP alias \"" + papAlias + "\" not found");
        
        return pap;
    }
    
    public boolean exists(String papAlias) {
    	
    	boolean result = keyExists(dnKey(papAlias));
    	
    	Log.debug("Call to exists(\"" + papAlias + "\": result=" + result);
    	
        return result;
    }

    public void store(PAP pap) throws AlreadyExistsException,
			RepositoryException {

		String papAlias = pap.getAlias();

		if (exists(papAlias))
			throw new AlreadyExistsException("Already exists: papAlias="
					+ papAlias);

		File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
		if (!directory.mkdir())
			throw new RepositoryException(String.format( "Cannot create directory for PAP: %s (id=%s) (dir=%s)", papAlias, pap.getPapId(), directory));

		savePAP(pap);
	}

    public void update(PAP pap) throws NotFoundException, RepositoryException {

		String papAlias = pap.getAlias();

		if (!exists(papAlias))
			throw new NotFoundException("PAP \"" + papAlias + "\" (id="
					+ pap.getPapId() + ")does not exists");

		savePAP(pap);
	}

    private void clearPAPProperties(String papAlias) {
        papsFile.clearProperty(dnKey(papAlias));
        papsFile.clearProperty(hostnameKey(papAlias));
        papsFile.clearProperty(portKey(papAlias));
        papsFile.clearProperty(pathKey(papAlias));
        papsFile.clearProperty(protocolKey(papAlias));
        papsFile.clearProperty(idKey(papAlias));
        papsFile.clearProperty(visibilityPublicKey(papAlias));
    }

    private String getPAPDirAbsolutePath(String papId) {
        return dbPath + File.separator + papId;
    }

    private boolean keyExists(String key) {
        
        if (key == null)
            return false;

        String value = papsFile.getString(key);

        if (value == null)
            return false;

        if (value.length() == 0)
            return false;

        return true;
    }
    
    private void savePAP(PAP pap) throws RepositoryException {

		if (pap == null)
			throw new RepositoryException(
					"Cannot save PAP because input parameter (PAP type) is null");

		setPAPProperties(pap);

		try {
			papsFile.save();
		} catch (ConfigurationException e) {
			throw new RepositoryException(e);
		}
	}

    private void setPAPProperties(PAP pap) {
    	
        String papAlias = pap.getAlias();
        
        papsFile.setProperty(dnKey(papAlias), pap.getDn());
        papsFile.setProperty(hostnameKey(papAlias), pap.getHostname());
        papsFile.setProperty(portKey(papAlias), pap.getPort());
        papsFile.setProperty(pathKey(papAlias), pap.getPath());
        papsFile.setProperty(protocolKey(papAlias), pap.getProtocol());
        papsFile.setProperty(idKey(papAlias), pap.getPapId());
        papsFile.setProperty(visibilityPublicKey(papAlias), pap.isVisibilityPublic());
    }
    
    private PAP getPAPFromProperies(String papAlias) {
        
        if (papAlias == null)
            return null;
        
        if (!exists(papAlias))
            return null;
        
        String dn = papsFile.getString(dnKey(papAlias));
        String host = papsFile.getString(hostnameKey(papAlias));
        String port = papsFile.getString(portKey(papAlias));
        String protocol = papsFile.getString(protocolKey(papAlias));
        String path = papsFile.getString(pathKey(papAlias));
        String id = papsFile.getString(idKey(papAlias));
        boolean visibilityPublic = papsFile.getBoolean(visibilityPublicKey(papAlias));
        
        PAP pap = new PAP(papAlias, dn, host, port, path, protocol, visibilityPublic);
        pap.setPapId(id);
        
        return pap;
    }
}
