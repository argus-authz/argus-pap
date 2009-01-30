package org.glite.authz.pap.repository.dao.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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

    public static FileSystemPAPDAO getInstance() {
        return new FileSystemPAPDAO();
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
        return aliasKey(papAlias) + "." + "visibility-puclic";
    }

    private INIConfiguration remotePAPsInfo;

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

    public void deleteByAlias(String papAlias) {

        if (!papExistsByAlias(papAlias))
            throw new NotFoundException("Not found: papAlias=" + papAlias);

        File papDir = new File(getPAPDirAbsolutePath(papAlias));
        for (File file : papDir.listFiles()) {
            file.delete();
        }
        papDir.delete();

        clearPAPProperties(papAlias);
        try {
            remotePAPsInfo.save();
        } catch (ConfigurationException e) {
            throw new RepositoryException(e);
        }
    }

    public void deleteById(String papId) {
        
        String papAlias = getAliasFromId(papId);
        
        if (papAlias == null)
            throw new NotFoundException("Not found: papId=" + papId);
        
        deleteByAlias(papAlias);
    }

    public List<PAP> getAll() {
        
        List<String> aliasList = getAllAliases();
        
        List<PAP> papList = new ArrayList<PAP>(aliasList.size());
        
        for (String alias:aliasList) {
            papList.add(getPAPFromProperies(alias));
        }

        return papList;
    }

    public List<String> getAllAliases() {
        List<String> idList = new LinkedList<String>();

        Iterator<String> iterator = remotePAPsInfo.getKeys(REMOTE_PAP_STANZA);

        while (iterator.hasNext()) {
            String key = iterator.next();
            int firstAliasChar = key.indexOf('.') + 1;
            String papAlias = key.substring(firstAliasChar, key.indexOf('.', firstAliasChar));
            idList.add(papAlias);
        }

        return idList;
    }

    public PAP getByAlias(String papAlias) {
        
        PAP pap = getPAPFromProperies(papAlias);
        
        if (pap == null)
            throw new NotFoundException("PAP alias \"" + papAlias + "\" not found");
        
        return pap;
    }
    
    public PAP getById(String papId) {
        return getByAlias(getAliasFromId(papId));
    }

    public boolean papExistsByAlias(String papAlias) {
        return keyExists(dnKey(papAlias));
    }

    public boolean papExistsById(String papId) {
        
        if (getAliasFromId(papId) != null)
            return true;

        return false;
    }

    public void store(PAP pap) {

        if (papExistsByAlias(pap.getAlias()))
            throw new AlreadyExistsException("Already exists: papAlias=" + pap.getAlias());

        File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
        if (!directory.mkdir())
            throw new RepositoryException("Cannot create directory for PAP: " + pap.getAlias() + pap.getPapId());

        savePAP(pap);
    }

    public void update(PAP pap) {
        
        if (!papExistsByAlias(pap.getAlias()))
            throw new NotFoundException("PAP \"" + pap.getAlias() + "\" (id=" + pap.getPapId() + ")does not exists");
        
        savePAP(pap);
    }

    private void clearPAPProperties(String papAlias) {
        remotePAPsInfo.clearProperty(dnKey(papAlias));
        remotePAPsInfo.clearProperty(hostnameKey(papAlias));
        remotePAPsInfo.clearProperty(portKey(papAlias));
        remotePAPsInfo.clearProperty(pathKey(papAlias));
        remotePAPsInfo.clearProperty(protocolKey(papAlias));
        remotePAPsInfo.clearProperty(idKey(papAlias));
        remotePAPsInfo.clearProperty(visibilityPublicKey(papAlias));
    }

    private String getAliasFromId(String papId) {
        
        if (papId == null)
            return null;

        List<String> aliasList = getAllAliases();

        for (String papAlias : aliasList) {

            if (papId.equals(remotePAPsInfo.getString(idKey(papAlias))))
                return papAlias;
        }

        return null;

    }

    private String getPAPDirAbsolutePath(String papId) {
        return dbPath + File.separator + papId;
    }

    private boolean keyExists(String key) {
        
        if (key == null)
            return false;

        String value = remotePAPsInfo.getString(key);

        if (value == null)
            return false;

        if (value.length() == 0)
            return false;

        return true;
    }
    
    private void savePAP(PAP pap) {
        
        setPAPProperties(pap);
        
        try {
            remotePAPsInfo.save();
        } catch (ConfigurationException e) {
            throw new RepositoryException(e);
        }
    }

    private void setPAPProperties(PAP pap) {
        String papAlias = pap.getAlias();
        remotePAPsInfo.setProperty(dnKey(papAlias), pap.getDn());
        remotePAPsInfo.setProperty(hostnameKey(papAlias), pap.getHostname());
        remotePAPsInfo.setProperty(portKey(papAlias), pap.getPort());
        remotePAPsInfo.setProperty(pathKey(papAlias), pap.getPath());
        remotePAPsInfo.setProperty(protocolKey(papAlias), pap.getProtocol());
        remotePAPsInfo.setProperty(idKey(papAlias), pap.getPapId());
        remotePAPsInfo.setProperty(visibilityPublicKey(papAlias), pap.isVisibilityPublic());
    }
    
    private PAP getPAPFromProperies(String papAlias) {
        
        if (papAlias == null)
            return null;
        
        if (!papExistsByAlias(papAlias))
            return null;
        
        String dn = remotePAPsInfo.getString(dnKey(papAlias));
        String host = remotePAPsInfo.getString(hostnameKey(papAlias));
        String port = remotePAPsInfo.getString(portKey(papAlias));
        String protocol = remotePAPsInfo.getString(protocolKey(papAlias));
        String path = remotePAPsInfo.getString(pathKey(papAlias));
        String id = remotePAPsInfo.getString(idKey(papAlias));
        boolean visibilityPublic = remotePAPsInfo.getBoolean(visibilityPublicKey(papAlias));
        
        PAP pap = new PAP(papAlias, dn, host, port, path, protocol, visibilityPublic);
        pap.setPapId(id);
        
        return pap;
        
    }

}
