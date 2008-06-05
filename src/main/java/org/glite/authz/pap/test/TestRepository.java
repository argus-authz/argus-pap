package org.glite.authz.pap.test;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.PolicySetImpl;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;

public class TestRepository {
	
	private static final String papPolicySetTemplatePath = "files/RootPAPPolicySetTemplate.xml";

	public static void main(String[] args) {

		DAOFactory daoFactory = DAOFactory.getDAOFactory();
		RootPolicySetDAO rootPolicySetDAO = daoFactory.getRootPolicySetDAO();
		
		if (!rootPolicySetDAO.existsRoot()) {
				rootPolicySetDAO.createRoot();
		}
		
		PolicySet localPAPPolicySet = new PolicySetImpl(papPolicySetTemplatePath);
		localPAPPolicySet.setId("LocalPolicySet");
		rootPolicySetDAO.createPAPAsFirst(localPAPPolicySet);
		
		PolicySet examplePolicySet = new PolicySetImpl(papPolicySetTemplatePath);
		examplePolicySet.setId("example_policyset_01");
		PolicySetDAO policySetDAO = daoFactory.getPolicySetDAO();
		policySetDAO.store(localPAPPolicySet.getId(), examplePolicySet);
		
	}
}
