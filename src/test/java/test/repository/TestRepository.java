package test.repository;

import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.PAPManager;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRepository {
	private static Logger log = LoggerFactory.getLogger( TestRepository.class );
	
	public static void main(String[] args) throws ConfigurationException {
		
		PAPConfiguration.bootstrap();
		
		PAPManager papManager = RepositoryManager.getPAPManager();
		
		List<PAPContainer> papContainerList = papManager.getAll();
		PAPContainer papContainer = null;
		if (papContainerList.isEmpty()) {
			log.info("No PAP present in the Repository");
			PAP newPAP = new PAP("Local");
			papContainer = papManager.create(newPAP);
			log.info("Created PAP: " + newPAP.getPapId());
			// Add a PolicySet
			PolicySetType policySet = PolicySetHelper.buildWithAnyTarget(papContainer.getPAP().getPapId(), PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
			papContainer.storePolicySet(policySet);
			// Add some Policy
			insertBunchOfPolicies(papContainer);
		} else {
			papContainer = papContainerList.get(0);
			for (PAPContainer papContainerElement:papContainerList) {
				log.info("Found PAP: " + papContainerElement.getPAP().getPapId());
				insertBunchOfPolicies(papContainerElement);
				deleteAllPolicies(papContainerElement);
			}
		}
	}
	
	private static void insertBunchOfPolicies(PAPContainer papContainer) {
		String papId = papContainer.getPAP().getPapId();
		for (int i=0; i<10; i++) {
			PolicyType policy = PolicyHelper.buildWithAnyTarget(papId + "_ex_" + i, PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);
			if (papContainer.hasPolicy(policy.getPolicyId())) {
				log.info("Policy \"" + policy.getPolicyId() + "\" already exists... action is overwrite it.");
			} else {
				log.info("Adding policy \"" + policy.getPolicyId() + "\" to PAP \"" + papId + "\" ");
			}
			papContainer.storePolicy(policy);
		}
	}
	
	private static void deleteAllPolicies(PAPContainer papContainer) {
		List<PolicyType> policies = papContainer.getAllPolicies();
		for (PolicyType policy:policies) {
			papContainer.deletePolicy(policy.getPolicyId());
			log.info("Deleted policy: " + policy.getPolicyId());
		}
	}
}
