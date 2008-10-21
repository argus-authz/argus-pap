package org.glite.authz.pap.repository.dao;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisioningServiceDAO {

    private static ProvisioningServiceDAO instance = null;
    private static final Logger log = LoggerFactory.getLogger(ProvisioningServiceDAO.class);
    public static ProvisioningServiceDAO getInstance() {
        if (instance == null)
            instance = new ProvisioningServiceDAO();
        return instance;
    }

    private final PAPManager papManager;

    private ProvisioningServiceDAO() {
        papManager = PAPManager.getInstance();
    }

    public List<XACMLObject> papQuery() {
        
        log.debug("Executing PAP query...");
        
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();
        PolicySetType rootPolicySet = makeRootPolicySet();
        List<PAPContainer> papContainerList = new LinkedList<PAPContainer>();
        
        papContainerList.add(papManager.getLocalPAPContainer());
        
        List<PAPContainer> publicPAPContainerList = papManager.getTrustedPAPContainerPublic();
        boolean useRootPolicySet = !publicPAPContainerList.isEmpty();
        
        if (useRootPolicySet) {
            resultList.add(rootPolicySet);
            papContainerList.addAll(publicPAPContainerList);
        }

        for (PAPContainer papContainer:papContainerList) {
            String papId = papContainer.getPAP().getPapId();
            
            if (useRootPolicySet)
                PolicySetHelper.addPolicySetReference(rootPolicySet, papId);
            
            resultList.addAll(getPublic(papContainer));
            
        }

        log.debug("PAP query executed: retrieved " + resultList.size() + " elements (Policy/PolicySet)");

        return resultList;
    }

    public List<XACMLObject> pdpQuery() {
        
        log.debug("Executing PDP query...");
        
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();
        
        PolicySetType rootPolicySet = makeRootPolicySet();
        
        resultList.add(rootPolicySet);
        
        List<PAPContainer> papContainerList = new LinkedList<PAPContainer>();
        
        papContainerList.add(papManager.getLocalPAPContainer());
        papContainerList.addAll(papManager.getTrustedPAPContainerAll());

        // Add references to the remote PAPs
        for (PAPContainer papContainer : papContainerList) {
            String papId = papContainer.getPAP().getPapId();

            PolicySetHelper.addPolicySetReference(rootPolicySet, papId);

            resultList.addAll(getAll(papContainer));
        }

        log.debug("PDP query executed: retrieved " + resultList.size()
                + " elemens (Policy/PolicySet) relate to " + papManager.getTrustedPAPContainerAll().size()
                + " PAPs");

        return resultList;
    }
    
    private List<XACMLObject> getAll(PAPContainer papContainer) {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();
        
        List<PolicySetType> policySetList = papContainer.getAllPolicySets();
        log.debug("Adding " + policySetList.size() + " PolicySet elements from PAP \"" + papContainer.getPAP().getPapId() + "\"");
        
        List<PolicyType> policyList = papContainer.getAllPolicies();
        log.debug("Adding " + policyList.size() + " Policy elements from PAP \"" + papContainer.getPAP().getPapId() + "\"");
        
        resultList.addAll(policySetList);
        resultList.addAll(policyList);
        
        return resultList;
    }
    
    private List<XACMLObject> getPublic(PAPContainer papContainer) {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();
        
        List<PolicySetType> policySetList = papContainer.getAllPolicySets();
        List<PolicyType> policyList = papContainer.getAllPolicies();
        
        for (PolicyType policy:policyList) {
            String policyId = policy.getPolicyId();
            
            if (!PolicyWizard.isPrivate(policyId))
                continue;
            
            policyList.remove(policy);
            
            for (PolicySetType policySet:policySetList) {
                if (PolicySetHelper.hasPolicyReferenceId(policySet, policyId))
                    PolicySetHelper.deletePolicyReference(policySet, policyId);
            }
        }
        
        log.debug("Adding " + policySetList.size() + " PolicySet elements from PAP \"" + papContainer.getPAP().getPapId() + "\"");
        resultList.addAll(policySetList);
        
        log.debug("Adding " + policyList.size() + " Policy elements from PAP \"" + papContainer.getPAP().getPapId() + "\"");
        resultList.addAll(policyList);
        
        return resultList;
    }
    
    private PolicySetType makeRootPolicySet() {
        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget("RootPolicySet_"
                + PAP.localPAPId, PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        return rootPolicySet;
    }
}
