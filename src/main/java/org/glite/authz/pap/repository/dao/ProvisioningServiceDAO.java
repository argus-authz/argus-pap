package org.glite.authz.pap.repository.dao;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.PAPManager;
import org.glite.authz.pap.repository.RepositoryManager;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;

public class ProvisioningServiceDAO {

    private static final ProvisioningServiceDAO instance = new ProvisioningServiceDAO();

    public static ProvisioningServiceDAO getInstance() {
        return instance;
    }

    private ProvisioningServiceDAO() {}

    public List<XACMLObject> papQuery() {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();
        
        PAP localPAP = PAP.makeLocalPAP();
        PAPContainer papContainer = RepositoryManager.getPAPManager().getContainer(localPAP);

        resultList.addAll(papContainer.getAllPolicySets());
        resultList.addAll(papContainer.getAllPolicies());

        return resultList;
    }

    public List<XACMLObject> pdpQuery() {
        List<XACMLObject> resultList = new LinkedList<XACMLObject>();

        // Create the root PolicySet
        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget("RootPolicySet_" + PAP.localPAPId,
                PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        resultList.add(rootPolicySet);

        // Add the first reference, which is the reference to the local PAP
        PolicySetHelper.addPolicySetReference(rootPolicySet, PAP.localPAPId);

        // Add references to the remote PAPs
        PAPManager papManager = RepositoryManager.getPAPManager();
        for (PAPContainer papContainer : papManager.getContainerAll()) {
            String papId = papContainer.getPAP().getPapId();
            
            if (!papId.equals(PAP.localPAPId))
                PolicySetHelper.addPolicySetReference(rootPolicySet, papId);
            
            resultList.addAll(papContainer.getAllPolicySets());
            resultList.addAll(papContainer.getAllPolicies());
        }

        return resultList;
    }
}
