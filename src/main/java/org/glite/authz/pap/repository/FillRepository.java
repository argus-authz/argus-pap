package org.glite.authz.pap.repository;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.common.utils.xacml.PolicyHelper;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.distribution.PAPManager;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FillRepository {
    
    private static final Logger log = LoggerFactory.getLogger(FillRepository.class);
    
    private FillRepository() {}
    
    public static void fillLocalPAP(int numberOfPolicySets, int numberOfPolicies) {
        fillPAP(PAP.localPAPAlias, numberOfPolicySets, numberOfPolicies);
    }
    
    public static void fillPAP(String papId, int numberOfPolicySets, int numberOfPolicies) {
        
        PAP pap = new PAP(null, papId, null);
        PAPManager pm = PAPManager.getInstance();
        
        PAPContainer container = null;
        
        if (pm.exists(pap.getPapId())) {
            container = pm.getTrustedPAPContainer(pap.getPapId());
            log.info("Deleting all policies and policy sets for PAP: " + papId);
            container.deleteAllPolicies();
            container.deleteAllPolicySets();
        } else {
            log.info("Creating pap: " + papId);
            container = pm.addTrustedPAP(pap);
        }
        
        PolicySetType policySet = PolicySetHelper.buildWithAnyTarget(papId, PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
        
        log.info("Storing " + numberOfPolicySets + " policy sets in PAP " + papId + "...");
        container.storePolicySet(policySet);
        for (int i=0; i<numberOfPolicySets; i++) {
            policySet = PolicySetHelper.buildWithAnyTarget(papId + "_ps_" + i, PolicySetHelper.COMB_ALG_ORDERED_DENY_OVERRIDS);
            container.storePolicySet(policySet);
        }
        
        log.info("Storing " + numberOfPolicies + " policies in PAP " + papId + "...");
        for (int i=0; i<numberOfPolicies; i++) {
            PolicyType policy = PolicyHelper.build(papId + "_p_" + i, PolicyHelper.RULE_COMBALG_DENY_OVERRIDS);
            container.storePolicy(policy);
        }
        
    }
    
    public static void fillFromConfiguration() {
        
        PAPConfiguration conf;
        
        try {
            conf = PAPConfiguration.instance();
        } catch (PAPConfigurationException e) {
            return;
        }
        
        if (!conf.getBoolean("configuration.test", false))
            return;
        
        int psN = conf.getInt("configuration.test.local_policyset_number");
        int pN = conf.getInt("configuration.test.local_policy_number");
        
        fillLocalPAP(psN, pN);
        
        int papN = conf.getInt("configuration.test.remote_pap_number");
        psN = conf.getInt("configuration.test.remote_policyset_number");
        pN = conf.getInt("configuration.test.remote_policy_number");

        for (int i=0; i< papN; i++) {
            fillPAP("remote_pap_" + i, psN, pN);
        }
    }
    
}
