package test.repository;

import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PAPPolicySetDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.dao.RootPolicySetDAO;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class TestRepository {
	
	public static void main(String[] args) {
		
		DAOFactory daoFactory = DAOFactory.getDAOFactory();
		RootPolicySetDAO rootPolicySetDAO = daoFactory.getRootPolicySetDAO();
		PAPPolicySetDAO papDAO = daoFactory.getPapDAO();
		
		if (!rootPolicySetDAO.exists()) {
				rootPolicySetDAO.create();
		}

		// Create a PAP PolicySet
		PolicySetType localPAPPolicySet = PolicySetHelper.build("LocalPolicySet", PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
		if (!papDAO.exists(localPAPPolicySet.getPolicySetId())) {
			papDAO.add(0, localPAPPolicySet);
		}
		
		// Insert PolicySet in the PAP
		PolicySetType examplePolicySet = PolicySetHelper.build("example_policyset_01", PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
		PolicySetDAO policySetDAO = daoFactory.getPolicySetDAO();
		policySetDAO.store(localPAPPolicySet.getPolicySetId(), examplePolicySet);
		
		// Insert Policy in the PAP
		PolicyDAO policyDAO = daoFactory.getPolicyDAO();
		PolicyType examplePolicy = PolicyHelper.build("example_policy_01", PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);
		policyDAO.store(localPAPPolicySet.getPolicySetId(), examplePolicy);
		
	}
}
