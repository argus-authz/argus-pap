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
    private static String REMOTE_PAP_STANZA = "remote-paps";
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

    public void deleteByAlias(String papAlias) throws NotFoundException, RepositoryException {

        if (!papExistsByAlias(papAlias))
            throw new NotFoundException("Not found: papAlias=" + papAlias);

        String papId = papsFile.getString(idKey(papAlias));
        
        deleteById(papId);
    }

    public void deleteById(String papId) throws NotFoundException, RepositoryException {
        
        String papAlias = getAliasFromId(papId);
        
        if (papAlias == null)
            throw new NotFoundException("Not found: papId=" + papId);

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

    public List<String> getAllAliases() {
        List<String> idList = new LinkedList<String>();

        Iterator<String> iterator = papsFile.getKeys(REMOTE_PAP_STANZA);

        while (iterator.hasNext()) {
            String key = iterator.next();
            int firstAliasChar = key.indexOf('.') + 1;
            String papAlias = key.substring(firstAliasChar, key.indexOf('.', firstAliasChar));
            idList.add(papAlias);
        }

        return idList;
    }

    public PAP getByAlias(String papAlias) throws NotFoundException {
        
        PAP pap = getPAPFromProperies(papAlias);
        
        if (pap == null)
            throw new NotFoundException("PAP alias \"" + papAlias + "\" not found");
        
        return pap;
    }
    
    public PAP getById(String papId) throws NotFoundException {
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

    public void store(PAP pap) throws AlreadyExistsException, RepositoryException {

        if (papExistsByAlias(pap.getAlias()))
            throw new AlreadyExistsException("Already exists: papAlias=" + pap.getAlias());

        File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
        if (!directory.mkdir())
            throw new RepositoryException("Cannot create directory for PAP: " + pap.getAlias() + " (id=" + pap.getPapId() + ")");

        savePAP(pap);
    }

    public void update(PAP pap) throws NotFoundException, RepositoryException {
        
        if (!papExistsByAlias(pap.getAlias()))
            throw new NotFoundException("PAP \"" + pap.getAlias() + "\" (id=" + pap.getPapId() + ")does not exists");
        
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

    private String getAliasFromId(String papId) {
        
        if (papId == null)
            return null;

        List<String> aliasList = getAllAliases();

        for (String papAlias : aliasList) {

            if (papId.equals(papsFile.getString(idKey(papAlias))))
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

        String value = papsFile.getString(key);

        if (value == null)
            return false;

        if (value.length() == 0)
            return false;

        return true;
    }
    
    private void savePAP(PAP pap) throws RepositoryException {
        
        if (pap == null)
            throw new RepositoryException("Cannot save PAP because input parameter (PAP type) is null");
        
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
        
        if (!papExistsByAlias(papAlias))
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
