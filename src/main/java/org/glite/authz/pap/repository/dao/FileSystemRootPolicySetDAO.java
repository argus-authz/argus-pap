package org.glite.authz.pap.repository.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.common.RepositoryConfiguration;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.common.xacml.PolicySetImpl;
import org.glite.authz.pap.common.xacml.IdReference;
import org.glite.authz.pap.common.xacml.XACMLObject;
import org.glite.authz.pap.repository.AlreadyExistsRepositoryException;
import org.glite.authz.pap.repository.RepositoryException;

public class FileSystemRootPolicySetDAO implements RootPolicySetDAO {

	public static FileSystemRootPolicySetDAO getInstance() {
		return new FileSystemRootPolicySetDAO();
	}

	private final String dbDir;
	private final String rootPolicySetFileNameAbsolutePath;
	private final PolicySetBuilder policySetBuilder;

	private FileSystemRootPolicySetDAO() {
		dbDir = RepositoryConfiguration.getFileSystemDatabaseDir();
		rootPolicySetFileNameAbsolutePath = dbDir + File.separator
				+ RepositoryConfiguration.getPolicySetFileNamePrefix()
				+ RepositoryConfiguration.getRootPolicySetFileName()
				+ RepositoryConfiguration.getXACMLFileNameExtension();
		policySetBuilder = RepositoryConfiguration.getPolicySetBuilder();
		if (!existsRoot()) {
			createRoot();
		}
	}

	public void createPAPAsFirst(PolicySet policySet) {
		createPAP(policySet);
		String papId = policySet.getId();
		PolicySet rootPS = getRoot();
		rootPS.insertPolicySetReferenceAsFirst(papId);
		updateRoot(rootPS);
	}

	public void createRoot() {
		if (!existsRoot()) {
			PolicySet rootPS = policySetBuilder.buildFromFile(RepositoryConfiguration.getRootPolicySetTemplatePath());
			rootPS.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
		} else {
			throw new AlreadyExistsRepositoryException();
		}
	}

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

	public boolean existsPAP(String papId) {
		PolicySet rootPolicySet = getRoot();
		return rootPolicySet.referenceIdExists(papId);
	}

	public boolean existsRoot() {
		return new File(this.rootPolicySetFileNameAbsolutePath).exists();
	}

	public PolicySet getPAPRoot(String papId) {
		return policySetBuilder.buildFromFile(getPAPFileNameAbsolutePath(papId));
	}

	public PolicySet getRoot() {
		return policySetBuilder.buildFromFile(rootPolicySetFileNameAbsolutePath);
	}

	public List<String> listPAPs() {
		PolicySet rootPolicySet = getRoot();
		List<XACMLObject> childrenList = rootPolicySet.getOrderedListOfXACMLObjectChildren();
		List<String> papList = new ArrayList<String>(childrenList.size());
		for (XACMLObject child : childrenList) {
			if (child.isPolicySetReference()) {
				papList.add(((IdReference) child).getValue());
			}
		}
		return papList;
	}

	public void updatePAP(String papId, PolicySet ps) {
		if (!existsPAP(papId)) {
			throw new RepositoryException("PAP does not exists");
		}
		ps.printXACMLDOMToFile(getPAPFileNameAbsolutePath(papId));
	}

	private void createPAP(PolicySet papRootPolicySet) {
		String papId = papRootPolicySet.getId();
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
		papRootPolicySet.printXACMLDOMToFile(papPolicySetFile.getAbsolutePath());
//		try {
//			//PolicySetImpl papPolicySetTemplate = new PolicySetImpl(RepositoryConfiguration.getPapPolicySetTemplatePath());
//		} catch (XACMLException e) {
//			throw new RepositoryException("Invalid XACML file: " + RepositoryConfiguration.getPapPolicySetTemplatePath(), e);
//		}
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

	private void updateRoot(PolicySet ps) {
		ps.printXACMLDOMToFile(this.rootPolicySetFileNameAbsolutePath);
	}
}
