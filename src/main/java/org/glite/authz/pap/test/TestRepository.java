package org.glite.authz.pap.test;

import org.glite.authz.pap.common.xacml.Policy;
import org.glite.authz.pap.common.xacml.PolicyBuilder;
import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetBuilder;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;

public class TestRepository {
	
	private static final String papPolicySetTemplatePath = "files/RootPAPPolicySetTemplate.xml";
	private static final String policyTemplate = "files/PolicyTemplate.xml";
	private static PolicySetBuilder policySetBuilder;
	private static PolicyBuilder policyBuilder;

	public static void main(String[] args) {
		
		policySetBuilder = RepositoryManager.getPolicySetBuilder();

		DAOFactory daoFactory = DAOFactory.getDAOFactory();
		RootPolicySetDAO rootPolicySetDAO = daoFactory.getRootPolicySetDAO();
		PAPPolicySetDAO papDAO = daoFactory.getPapDAO();
		
		if (!rootPolicySetDAO.exists()) {
				rootPolicySetDAO.create();
		}

		// Create a PAP PolicySet
		PolicySet localPAPPolicySet = policySetBuilder.buildFromFile(papPolicySetTemplatePath);
		localPAPPolicySet.setId("LocalPolicySet");
		if (!papDAO.exists(localPAPPolicySet.getId())) {
			papDAO.createAsFirst(localPAPPolicySet);
		}
		
		// Insert PolicySet in the PAP
		PolicySet examplePolicySet = policySetBuilder.buildFromFile(papPolicySetTemplatePath);
		examplePolicySet.setId("example_policyset_01");
		PolicySetDAO policySetDAO = daoFactory.getPolicySetDAO();
		policySetDAO.store(localPAPPolicySet.getId(), examplePolicySet);
		
		// Insert Policy in the PAP
		PolicyDAO policyDAO = daoFactory.getPolicyDAO();
		Policy examplePolicy = policyBuilder.buildFromFile(policyTemplate);
		examplePolicy.setId("example_policy_01");
		policyDAO.store(localPAPPolicySet.getId(), examplePolicy);
		
	}
}
