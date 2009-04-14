package org.glite.authz.pap.authz.operations.policyprovisioning;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.distribution.DistributionModule;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.services.ServicesUtils;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

/**
 * Authorized operation to fetch policies out of this pap when the caller is a PDP.
 * 
 */
public class GetPoliciesForPDPOperation extends BasePAPOperation<List<XACMLObject>> {

    private GetPoliciesForPDPOperation() {

    }

    public static GetPoliciesForPDPOperation instance() {

        return new GetPoliciesForPDPOperation();
    }

    @Override
    protected List<XACMLObject> doExecute() {

        log.debug("Executing PDP query...");

        List<XACMLObject> resultList = new LinkedList<XACMLObject>();

        PolicySetType rootPolicySet = ServicesUtils.makeRootPolicySet();

        resultList.add(rootPolicySet);

        List<PapContainer> papContainerList = PapContainer.getContainers(PapManager.getInstance()
                                                                                   .getAllPaps());

        // Add references to the remote PAPs
        for (PapContainer papContainer : papContainerList) {
            
            if (!papContainer.getPap().isEnabled()) {
                continue;
            }
            
            log.info("Adding PAP: " + papContainer.getPap().getAlias());

            try {
                PolicySetType papPolicySetNoReferences;

                synchronized (DistributionModule.storePoliciesLock) {
                    papPolicySetNoReferences = getPolicySetNoReferences(papContainer,
                                                                        papContainer.getRootPolicySetId());
                }

                PolicySetHelper.addPolicySet(rootPolicySet, papPolicySetNoReferences);

                TypeStringUtils.releaseUnneededMemory(papPolicySetNoReferences);

            } catch (NotFoundException e) {
                continue;
            }
        }

        TypeStringUtils.releaseUnneededMemory(rootPolicySet);

        log.debug("PDP query executed: retrieved " + resultList.size() + " elements (Policy/PolicySet)");

        return resultList;
    }

    private PolicySetType getPolicySetNoReferences(PapContainer papContainer, String policySetId) {

        PolicySetType policySetNoRef = papContainer.getPolicySet(policySetId);

        // replace policy set references with policy sets
        List<String> idReferenceList = PolicySetHelper.getPolicySetIdReferencesValues(policySetNoRef);
        for (String childPolicySetId : idReferenceList) {

            try {
                PolicySetType childPolicySetNoRef = getPolicySetNoReferences(papContainer, childPolicySetId);

                PolicySetHelper.addPolicySet(policySetNoRef, childPolicySetNoRef);

                TypeStringUtils.releaseUnneededMemory(childPolicySetNoRef);
            } catch (NotFoundException e) {
                // this exception might occur in case of concurrent remove/add policy operations
                // nothing to do, go on and remove the reference
            }

            PolicySetHelper.deletePolicySetReference(policySetNoRef, childPolicySetId);
        }

        // replace policy references with policies
        idReferenceList = PolicySetHelper.getPolicyIdReferencesValues(policySetNoRef);
        for (String policyIdReference : idReferenceList) {

            try {
                PolicyType policy = papContainer.getPolicy(policyIdReference);

                PolicySetHelper.addPolicy(policySetNoRef, policy);

                TypeStringUtils.releaseUnneededMemory(policy);
            } catch (NotFoundException e) {
                // this exception might occur in case of concurrent remove/add policy operations
                // nothing to do, go on and remove the reference
            }

            PolicySetHelper.deletePolicyReference(policySetNoRef, policyIdReference);
        }

        return policySetNoRef;
    }

    @Override
    /*
     * * The required permission for this operation is:
     * <code>POLICY_READ_LOCAL|POLICY_READ_REMOTE</code>
     */
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_READ_LOCAL,
                                               PermissionFlags.POLICY_READ_REMOTE));
    }
}
