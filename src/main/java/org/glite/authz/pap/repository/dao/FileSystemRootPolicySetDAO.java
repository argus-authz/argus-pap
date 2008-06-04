package org.glite.authz.pap.repository.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.RepositoryConfiguration;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetImpl;
import org.glite.authz.pap.common.xacml.ReferenceId;
import org.glite.authz.pap.common.xacml.XACMLException;
import org.glite.authz.pap.common.xacml.XACMLObject;
import org.glite.authz.pap.repository.AlreadyExistsRepositoryException;
import org.glite.authz.pap.repository.RepositoryException;

public class FileSystemRootPolicySetDAO implements RootPolicySetDAO {

	public static FileSystemRootPolicySetDAO getInstance() {
		return new FileSystemRootPolicySetDAO();
	}

	private String dbDir;
	private String rootPolicySetFileNameAbsolutePath;

	private FileSystemRootPolicySetDAO() {
		this.dbDir = RepositoryConfiguration.getFileSystemDatabaseDir();
		this.rootPolicySetFileNameAbsolutePath = dbDir + File.separator
				+ RepositoryConfiguration.getPolicySetFileNamePrefix()
				+ RepositoryConfiguration.getRootPolicySetFileName()
				+ RepositoryConfiguration.getXACMLFileNameExtension();

		if (!existsRoot()) {
			createRoot();
		}
	}

	@Override
	public void createPAPAsFirst(String papId) {
		createPAP(papId);
		PolicySet rootPS = getRoot();
		rootPS.insertPolicySetReferenceAsFirst(papId);
		updateRoot(rootPS);
	}

	@Override
	public void createRoot() {
		if (!existsRoot()) {
			PolicySetImpl rootPS = new PolicySetImpl(RepositoryConfiguration.getRootPolicySetTemplatePath());
			rootPS.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
		} else {
			throw new AlreadyExistsRepositoryException();
		}
	}

	@Override
	public void deletePAP(String papId) {
		if (existsPAP(papId)) {
			PolicySet rootPolicySet = getRoot();
			rootPolicySet.deletePolicySetReference(papId);
			updateRoot(rootPolicySet);
			File papDir = new File(getPAPDirAbsolutePath(papId));
			for (File file : papDir.listFiles()) {
				file.delete();
			}
			papDir.delete();
		}
	}

	@Override
	public boolean existsPAP(String papId) {
		PolicySet rootPolicySet = getRoot();
		return rootPolicySet.referenceIdExists(papId);
	}

	@Override
	public boolean existsRoot() {
		return new File(this.rootPolicySetFileNameAbsolutePath).exists();
	}

	@Override
	public PolicySet getPAPRoot(String papId) {
		return new PolicySetImpl(getPAPFileNameAbsolutePath(papId));
	}

	@Override
	public PolicySet getRoot() {
		return new PolicySetImpl(this.rootPolicySetFileNameAbsolutePath);
	}

	@Override
	public List<String> listPAPs() {
		PolicySet rootPolicySet = getRoot();
		List<XACMLObject> childrenList = rootPolicySet.getOrderedListOfXACMLObjectChildren();
		List<String> papList = new ArrayList<String>(childrenList.size());
		for (XACMLObject child : childrenList) {
			if (child.isPolicySetReference()) {
				papList.add(((ReferenceId) child).getValue());
			}
		}
		return papList;
	}

	@Override
	public void updatePAP(String papId, PolicySet ps) {
		if (!existsPAP(papId)) {
			throw new RepositoryException("PAP does not exists");
		}
		ps.printXACMLDOMToFile(getPAPFileNameAbsolutePath(papId));
	}

	private void updateRoot(PolicySet ps) {
		ps.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
	}

	/**
	 * @param papId
	 * @throws RepositoryException
	 *             Cannot write, invalid XACML
	 * @throws AlreadyExistsRepositoryException
	 */
	private void createPAP(String papId) {
		if (existsPAP(papId)) {
			throw new AlreadyExistsRepositoryException();
		}
		File directory = new File(getPAPDirAbsolutePath(papId));
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				throw new RepositoryException("Cannot create directory for PAP: " + papId);
			}
		}
		File papPolicySetFile = new File(getPAPFileNameAbsolutePath(papId));
		try {
			PolicySetImpl papPolicySetTemplate = new PolicySetImpl(RepositoryConfiguration.getPapPolicySetTemplatePath());
			papPolicySetTemplate.setId(papId);
			papPolicySetTemplate.printXACMLDOMToFile(papPolicySetFile.getAbsolutePath());
		} catch (XACMLException e) {
			throw new RepositoryException("Invalid XACML file: " + RepositoryConfiguration.getPapPolicySetTemplatePath(), e);
		}
	}

	private String getPAPDirAbsolutePath(String papId) {
		return dbDir + File.separator + papId;
	}

	private String getPAPFileNameAbsolutePath(String papId) {
		String fileName = RepositoryConfiguration.getPolicySetFileNamePrefix()
				+ RepositoryConfiguration.getRootPAPPolicySetId()
				+ RepositoryConfiguration.getXACMLFileNameExtension();
		return getPAPDirAbsolutePath(papId) + File.separator + fileName;
	}
}
