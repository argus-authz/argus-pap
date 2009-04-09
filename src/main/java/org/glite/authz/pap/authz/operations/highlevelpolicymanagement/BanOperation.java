package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.TargetWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class BanOperation extends BasePAPOperation<String> {

    private String alias;
    private AttributeWizard actionAttributeWizard;
    private AttributeWizard banAttributeWizard;
    private boolean isPublic;
    private AttributeWizard resourceAttributeWizard;

    protected BanOperation(String alias, AttributeWizard banAttributeWizard, AttributeWizard resourceAttributeWizard,
            AttributeWizard actionAttributeWizard, boolean isPublic) {

        this.alias = alias;
        this.banAttributeWizard = banAttributeWizard;
        this.resourceAttributeWizard = resourceAttributeWizard;
        this.actionAttributeWizard = actionAttributeWizard;
        this.isPublic = isPublic;
    }

    public static BanOperation instance(String alias, AttributeWizard banAttributeWizard,
            AttributeWizard resourceAttributeWizard, AttributeWizard actionAttributeWizard, boolean isPublic) {
        return new BanOperation(alias, banAttributeWizard, resourceAttributeWizard, actionAttributeWizard, isPublic);
    }

    protected String doExecute() {

        boolean policySetNeedToBeSaved = true;
        boolean updateOperationForPolicySet = false;
        boolean updateOperationForPolicy = false;

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }
        
        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        PolicySetType targetPolicySet = getTargetPolicySet(papContainer);

        if (targetPolicySet == null) {
            targetPolicySet = (new PolicySetWizard(resourceAttributeWizard)).getXACML();
        } else {
            updateOperationForPolicySet = true;
        }

        String policyId = null;

        PolicyWizard targetPolicyWizard;
        PolicyType candidatePolicy = getTargetPolicy(papContainer, targetPolicySet);

        if (candidatePolicy == null) {
            targetPolicyWizard = new PolicyWizard(actionAttributeWizard);
            targetPolicyWizard.setPrivate(!isPublic);
            policyId = targetPolicyWizard.getPolicyId();
            PolicySetHelper.addPolicyReference(targetPolicySet, 0, policyId);
        } else {
            targetPolicyWizard = new PolicyWizard(candidatePolicy);

            if (targetPolicyWizard.denyRuleForAttributeExists(banAttributeWizard)) {
                // ban policy already exists
                return null;
            }
            policyId = candidatePolicy.getPolicyId();
            updateOperationForPolicy = true;
            policySetNeedToBeSaved = false;
        }

        targetPolicyWizard.addRule(0, banAttributeWizard, EffectType.Deny);

        // Store the ban policy and the policy set in which it is contained
        // (only if needed)
        if (policySetNeedToBeSaved) {
            if (updateOperationForPolicySet) {
                String oldVersion = targetPolicySet.getVersion();
                PolicySetWizard.increaseVersion(targetPolicySet);
                papContainer.updatePolicySet(oldVersion, targetPolicySet);
            } else {
                papContainer.addPolicySet(0, targetPolicySet);
            }
        } else {
            TypeStringUtils.releaseUnneededMemory(targetPolicySet);
        }

        if (updateOperationForPolicy) {
            String oldVersion = targetPolicyWizard.getVersionString();
            targetPolicyWizard.increaseVersion();
            papContainer.updatePolicy(oldVersion, targetPolicyWizard.getXACML());
        } else {
            papContainer.storePolicy(targetPolicyWizard.getXACML());
        }

        targetPolicyWizard.releaseChildrenDOM();
        targetPolicyWizard.releaseDOM();

        return policyId;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }

    private PolicyType getTargetPolicy(PapContainer papContainer, PolicySetType policySet) {

        List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(policySet);

        if (policyIdList.size() == 0) {
            return null;
        }

        // get the target policy, it must be the very first policy
        PolicyType candidatePolicy = papContainer.getPolicy(policyIdList.get(0));

        PolicyType policy = null;;
        TargetWizard policyTargetWizard = new TargetWizard(actionAttributeWizard);

        if (policyTargetWizard.isEquivalent(candidatePolicy.getTarget())) {

            policy = candidatePolicy;

            if (PolicyWizard.isPublic(policy.getPolicyId()) != isPublic) {
                return null;
            }
        }
        return policy;
    }

    private PolicySetType getTargetPolicySet(PapContainer papContainer) {

        // get the target policy set, it must be the very first policy set (if it exists)
        PolicySetType targetPolicySet = null;
        TargetWizard policySetTargetWizard = new TargetWizard(resourceAttributeWizard);

        PolicySetType papRootPolicySet = papContainer.getRootPolicySet();

        List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(papRootPolicySet);

        TypeStringUtils.releaseUnneededMemory(papRootPolicySet);

        if (policySetIdList.size() == 0) {
            return null;
        }

        PolicySetType candidatePolicySet = papContainer.getPolicySet(policySetIdList.get(0));

        if (policySetTargetWizard.isEquivalent(candidatePolicySet.getTarget())) {
            targetPolicySet = candidatePolicySet;
        }

        return targetPolicySet;
    }
}
