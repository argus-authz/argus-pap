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
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.dao.PAPDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;

public class FileSystemPAPDAO implements PAPDAO {

	private static String dbPath = FileSystemRepositoryManager.getFileSystemDatabaseDir();
	private static FileSystemPAPDAO instance = null;
	
	private static final String PAP_FILE_NAME = "pap_info.ini";
	private static final String REMOTE_PAP_STANZA = "remote-paps";

	private INIConfiguration papsINIFile;

	private FileSystemPAPDAO() {
		papsINIFile = new INIConfiguration();

		File iniPAPConfigurationFile = new File(dbPath + File.separator + PAP_FILE_NAME);

		papsINIFile.setFile(iniPAPConfigurationFile);

		try {

			if (!iniPAPConfigurationFile.exists())
				papsINIFile.save();

			papsINIFile.load();
		} catch (ConfigurationException e) {
			throw new RepositoryException("Configuration error", e);
		}

	}

	public static FileSystemPAPDAO getInstance() {
		if (instance == null)
			instance = new FileSystemPAPDAO();
		return instance;
	}

	private static String aliasAlreadyExistsExceptionMsg(String papAlias) {
		return "Already exists: papAlias=" + papAlias;
	}

	private static String aliasKey(String papAlias) {
		return REMOTE_PAP_STANZA + "." + papAlias;
	}

	private static String aliasNotFoundExceptionMsg(String papAlias) {
		return "Not found: papAlias=" + papAlias;
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

	public void delete(String papAlias) throws NotFoundException, RepositoryException {

		if (!exists(papAlias))
			throw new NotFoundException(aliasNotFoundExceptionMsg(papAlias));

		String papId = papsINIFile.getString(idKey(papAlias));
		
		PAPContainer papContainer = new PAPContainer(get(papAlias));
		
		papContainer.deleteAllPolicies();
		papContainer.deleteAllPolicySets();
		
		File papDir = new File(getPAPDirAbsolutePath(papId));
		papDir.delete();

		removeFromINIFile(papAlias);
	}

	public boolean exists(String papAlias) {

		return existsInINIFile(papAlias);
	}

	public PAP get(String papAlias) {

		PAP pap = getPAPFromINIFile(papAlias);

		if (pap == null)
			throw new NotFoundException(aliasNotFoundExceptionMsg(papAlias));

		return pap;
	}

	public List<PAP> getAll() {

		List<String> aliasList = getAllAliases();

		List<PAP> papList = new ArrayList<PAP>(aliasList.size());

		for (String alias : aliasList) {
			papList.add(getPAPFromINIFile(alias));
		}

		return papList;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllAliases() {

		Set<String> aliasSet = new HashSet<String>();

		Iterator<String> iterator = papsINIFile.getKeys(REMOTE_PAP_STANZA);
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

	public void store(PAP pap) {

		String papAlias = pap.getAlias();

		if (exists(papAlias))
			throw new AlreadyExistsException(aliasAlreadyExistsExceptionMsg(papAlias));

		File directory = new File(getPAPDirAbsolutePath(pap.getPapId()));
		if (!directory.mkdir())
			throw new RepositoryException(String.format(
					"Cannot create directory for PAP: %s (id=%s) (dir=%s)", papAlias, pap.getPapId(),
					directory));

		saveToINIFile(pap);
	}

	public void update(PAP pap) {

		String papAlias = pap.getAlias();

		if (!exists(papAlias))
			throw new NotFoundException(aliasNotFoundExceptionMsg(papAlias));

		saveToINIFile(pap);
	}

	private void clearPAPProperties(String papAlias) {
		papsINIFile.clearProperty(dnKey(papAlias));
		papsINIFile.clearProperty(hostnameKey(papAlias));
		papsINIFile.clearProperty(portKey(papAlias));
		papsINIFile.clearProperty(pathKey(papAlias));
		papsINIFile.clearProperty(protocolKey(papAlias));
		papsINIFile.clearProperty(idKey(papAlias));
		papsINIFile.clearProperty(visibilityPublicKey(papAlias));
	}

	private boolean existsInINIFile(String papAlias) {
		return keyExists(dnKey(papAlias));
	}

	private String getPAPDirAbsolutePath(String papId) {
		return dbPath + File.separator + papId;
	}

	private PAP getPAPFromINIFile(String papAlias) {

		if (papAlias == null)
			return null;

		if (!exists(papAlias))
			return null;

		String dn = papsINIFile.getString(dnKey(papAlias));
		String host = papsINIFile.getString(hostnameKey(papAlias));
		String port = papsINIFile.getString(portKey(papAlias));
		String protocol = papsINIFile.getString(protocolKey(papAlias));
		String path = papsINIFile.getString(pathKey(papAlias));
		String id = papsINIFile.getString(idKey(papAlias));
		boolean visibilityPublic = papsINIFile.getBoolean(visibilityPublicKey(papAlias));

		PAP pap = new PAP(papAlias, dn, host, port, path, protocol, visibilityPublic);
		pap.setPapId(id);

		return pap;
	}

	private boolean keyExists(String key) {

		if (key == null)
			return false;

		String value = papsINIFile.getString(key);

		if (value == null)
			return false;

		if (value.length() == 0)
			return false;

		return true;
	}

	private void removeFromINIFile(String papAlias) {

		clearPAPProperties(papAlias);

		try {
			papsINIFile.save();
		} catch (ConfigurationException e) {
			throw new RepositoryException(e);
		}
	}

	private void saveToINIFile(PAP pap) throws RepositoryException {

		if (pap == null)
			throw new RepositoryException("BUG: PAP is null");

		setPAPProperties(pap);

		try {
			papsINIFile.save();
		} catch (ConfigurationException e) {
			throw new RepositoryException(e);
		}
	}

	private void setPAPProperties(PAP pap) {

		String papAlias = pap.getAlias();

		papsINIFile.setProperty(dnKey(papAlias), pap.getDn());
		papsINIFile.setProperty(hostnameKey(papAlias), pap.getHostname());
		papsINIFile.setProperty(portKey(papAlias), pap.getPort());
		papsINIFile.setProperty(pathKey(papAlias), pap.getPath());
		papsINIFile.setProperty(protocolKey(papAlias), pap.getProtocol());
		papsINIFile.setProperty(idKey(papAlias), pap.getPapId());
		papsINIFile.setProperty(visibilityPublicKey(papAlias), pap.isVisibilityPublic());
	}
}
