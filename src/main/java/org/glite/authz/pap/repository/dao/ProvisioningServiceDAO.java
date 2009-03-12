package org.glite.authz.pap.repository.dao;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
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

        List<PAPContainer> papContainerList = new LinkedList<PAPContainer>();
        papContainerList.add(papManager.getLocalPAPContainer());

        List<PAPContainer> publicPAPContainerList = papManager.getPublicRemotePAPsContainers();

        boolean useRootPolicySet = !(publicPAPContainerList.isEmpty());

        PolicySetType rootPolicySet = makeRootPolicySet();

        if (useRootPolicySet) {
            resultList.add(rootPolicySet);
            papContainerList.addAll(publicPAPContainerList);
        }

        for (PAPContainer papContainer : papContainerList) {

            String papId = papContainer.getPAP().getPapId();

            if (useRootPolicySet) {
                PolicySetHelper.addPolicySetReference(rootPolicySet, papId);
            }
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

        PAPContainer[] papContainerList = papManager.getOrderedPAPContainerArray();

        // Add references to the remote PAPs
        for (PAPContainer papContainer : papContainerList) {
            log.info("Adding PAP: " + papContainer.getPAP().getAlias());

            try {
                PolicySetType papPolicySetNoReferences = getPolicySetNoReferences(papContainer,
                                                                                  papContainer.getPAPRootPolicySetId());
                PolicySetHelper.addPolicySet(rootPolicySet, papPolicySetNoReferences);

                TypeStringUtils.releaseUnnecessaryMemory(papPolicySetNoReferences);

            } catch (NotFoundException e) {
                continue;
            }
        }

        TypeStringUtils.releaseUnnecessaryMemory(rootPolicySet);

        log.debug("PDP query executed: retrieved " + resultList.size() + " elemens (Policy/PolicySet) relate to "
                + papManager.getOrderedRemotePAPsContainerArray().length + " PAPs");

        return resultList;
    }

    private PolicySetType getPolicySetNoReferences(PAPContainer papContainer, String policySetId) {

        PolicySetType policySetNoRef = TypeStringUtils.cloneAsPolicySetTypeString(papContainer.getPolicySet(policySetId));

        // replace policy set references with policy sets
        List<String> idReferenceList = PolicySetHelper.getPolicySetIdReferencesValues(policySetNoRef);
        for (String childPolicySetId : idReferenceList) {

            PolicySetType childPolicySetNoRef = getPolicySetNoReferences(papContainer, childPolicySetId);

            PolicySetHelper.addPolicySet(policySetNoRef, childPolicySetNoRef);

            TypeStringUtils.releaseUnnecessaryMemory(childPolicySetNoRef);

            PolicySetHelper.deletePolicySetReference(policySetNoRef, childPolicySetId);
        }

        // replace policy references with policies
        idReferenceList = PolicySetHelper.getPolicyIdReferencesValues(policySetNoRef);
        for (String policyIdReference : idReferenceList) {

            PolicyType policy = papContainer.getPolicy(policyIdReference);

            PolicySetHelper.addPolicy(policySetNoRef, policy);

            TypeStringUtils.releaseUnnecessaryMemory(policy);

            PolicySetHelper.deletePolicyReference(policySetNoRef, policyIdReference);
        }

        return policySetNoRef;
    }

    // pdpQuery() was modified in order to do not use references, therefore this
    // method is no more used... consider to delete it before or later...
    @SuppressWarnings("unused")
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

        boolean removedAtLeastOnePrivatePolicy = false;

        // Remove all private policies from the list (together with its references in the policy sets)
        for (PolicyType policy : policyList) {
            String policyId = policy.getPolicyId();

            TypeStringUtils.releaseUnnecessaryMemory(policy);

            if (PolicyWizard.isPublic(policyId)) {
                continue;
            }

            // Remove private policy from the list
            policyList.remove(policy);

            // Remove references of the policy
            for (PolicySetType policySet : policySetList) {
                if (PolicySetHelper.hasPolicyReferenceId(policySet, policyId)) {
                    PolicySetHelper.deletePolicyReference(policySet, policyId);
                    removedAtLeastOnePrivatePolicy = true;
                }
            }
        }

        if (removedAtLeastOnePrivatePolicy) {
            for (PolicySetType policySet : policySetList) {
                TypeStringUtils.releaseUnnecessaryMemory(policySet);
            }
        }

        log.debug("Adding " + policySetList.size() + " PolicySet elements from PAP \"" + papContainer.getPAP().getPapId() + "\"");
        resultList.addAll(policySetList);

        log.debug("Adding " + policyList.size() + " Policy elements from PAP \"" + papContainer.getPAP().getPapId() + "\"");
        resultList.addAll(policyList);

        return resultList;
    }

    private PolicySetType makeRootPolicySet() {
        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget("RootPolicySet_" + PAP.LOCAL_PAP_ALIAS,
                                                                         PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        return rootPolicySet;
    }
}
