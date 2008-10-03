package org.glite.authz.pap.repository.dao;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisioningServiceDAO {

    private static final Logger log = LoggerFactory.getLogger(ProvisioningServiceDAO.class);
    private static final ProvisioningServiceDAO instance = new ProvisioningServiceDAO();

    public static ProvisioningServiceDAO getInstance() {
        return instance;
    }

    private ProvisioningServiceDAO() {}

    public List<XACMLObject> papQuery() {
        
        log.debug("Executing PAP query...");
        
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();

        PAPContainer papContainer = PAPManager.getInstance().getLocalPAPContainer();

        resultList.addAll(papContainer.getAllPolicySets());
        resultList.addAll(papContainer.getAllPolicies());

        log.debug("PAP query executed: retrieved " + resultList.size() + " elemens (Policy/PolicySet)");

        return resultList;
    }

    public List<XACMLObject> pdpQuery() {
        
        log.debug("Executing PDP query...");
        
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();
        
        // Create the root PolicySet
        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget("RootPolicySet_"
                + PAP.localPAPId, PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        resultList.add(rootPolicySet);

        // Add the first reference, which is the reference to the local PAP
        PolicySetHelper.addPolicySetReference(rootPolicySet, PAP.localPAPId);

        // Add references to the remote PAPs
        PAPManager papManager = PAPManager.getInstance();
        for (PAPContainer papContainer : papManager.getContainerAll()) {
            String papId = papContainer.getPAP().getPapId();

            if (!papId.equals(PAP.localPAPId))
                PolicySetHelper.addPolicySetReference(rootPolicySet, papId);

            List<PolicySetType> policySetList = papContainer.getAllPolicySets();
            log.debug("Retrieved " + policySetList.size() + " PolicySet from PAP " + papId);
            
            List<PolicyType> policyList = papContainer.getAllPolicies();
            log.debug("Retrieved " + policyList.size() + " Policy from PAP " + papId);
            
            
            resultList.addAll(policySetList);
            resultList.addAll(policyList);
            
        }

        log.debug("PDP query executed: retrieved " + resultList.size()
                + " elemens (Policy/PolicySet) relate to " + papManager.getContainerAll().size()
                + " PAPs");

        return resultList;
    }
}
