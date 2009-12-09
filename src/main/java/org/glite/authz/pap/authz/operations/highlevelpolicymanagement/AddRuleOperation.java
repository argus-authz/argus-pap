package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.RuleWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.exceptions.HighLevelPolicyManagementServiceException;
import org.glite.authz.pap.services.exceptions.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class AddRuleOperation extends BasePAPOperation<String> {

    private String actionId;
    private String actionValue;
    private String resourceValue;
    private String alias;
    private List<AttributeWizard> attributeWizardList;
    private EffectType effect;
    private boolean after;
    private String ruleId;

    protected AddRuleOperation(String alias, boolean isPermit, List<AttributeWizard> attributeWizardList,
            String actionValue, String resourceValue, String actionId, String ruleId, boolean after) {

        this.alias = alias;
        this.attributeWizardList = attributeWizardList;
        this.actionId = actionId;
        this.actionValue = actionValue;
        this.resourceValue = resourceValue;
        this.ruleId = ruleId;
        this.after = after;

        if (isPermit) {
            effect = EffectType.Permit;
        } else {
            effect = EffectType.Deny;
        }

    }

    public static AddRuleOperation instance(String alias, boolean isPermit,
            List<AttributeWizard> attributeWizardList, String actionValue, String resourceValue,
            String actionId, String ruleId, boolean after) {
        return new AddRuleOperation(alias,
                                    isPermit,
                                    attributeWizardList,
                                    actionValue,
                                    resourceValue,
                                    actionId,
                                    ruleId,
                                    after);
    }

    protected String doExecute() {

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }

        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }
        
        if ((actionId == null) && (actionValue == null)) {
            throw new XACMLPolicyManagementServiceException("Action id and action value are both unspecified.");
        }
        
        RuleWizard ruleWizard = new RuleWizard(attributeWizardList, effect);
        
        PapContainer papContainer = new PapContainer(pap);
        
        if (actionId == null) {
            actionId = getActionId(papContainer, actionValue, resourceValue, after);
        }
        
        PolicyType policy = papContainer.getPolicy(actionId);

        int index = 0;

        if (ruleId != null) {

            index = PolicyHelper.indexOfRule(policy, ruleId);

            if (index == -1) {
                throw new XACMLPolicyManagementServiceException("ruleId not found: " + ruleId);
            }

            if (after) {
                index++;
            }
        }

        if ((ruleId == null) && (after)) {
            index = -1;
        }

        PolicyHelper.addRule(policy, index, ruleWizard.getXACML());

        TypeStringUtils.releaseUnneededMemory(policy);

        papContainer.updatePolicy(policy);

        return ruleWizard.getRuleId();
    }

    /**
     * Return the id of the policy identified by the couple action value, resource value. The action
     * value or the resource value cannot be <code>null</code>. The returned action-id is the id of
     * the action having the given action value and found inside a resource having the given
     * resource value.
     * <p>
     * If a resource/action with the given value is not found a new resource/action is created.
     * 
     * @param papContainer the pap to add the rule in.
     * @param actionValue value of the action.
     * @param resourceValue value of the resource.
     * @param bottom a new resource/action is inserted at the top if this parameter is set to
     *            <code>true</code>, at the bottom otherwise. <i>bottom</i> parameter.
     * @return  the id of the policy identified by the couple action value, resource value.
     * 
     * @throws HighLevelPolicyManagementServiceException if there is more than one resource or
     *             action with the given value.
     * @throws HighLevelPolicyManagementServiceException if the action value or the resource value
     *             are <code>null</code>.
     */
    private String getActionId(PapContainer papContainer, String actionValue, String resourceValue,
            boolean bottom) {

        List<PolicySetType> policySetList = papContainer.getAllPolicySets();

        PolicySetType targetPolicySet = null;

        // skipping the first policy set the is the root policy set
        for (int i = 1; i < policySetList.size(); i++) {
            PolicySetType policySet = policySetList.get(i);
            if (resourceValue.equals(PolicySetWizard.getResourceValue(policySet))) {
                if (targetPolicySet != null) {
                    throw new HighLevelPolicyManagementServiceException("More than one resource with the same value");
                }
                targetPolicySet = policySet;
            }
        }
        
        if (targetPolicySet == null) {
            String resourceId = createResource(papContainer, resourceValue, bottom);
            return createAction(papContainer, resourceId, actionValue, bottom);
        }
        
        policySetList = null;
        
        List<String> policyIdList = PolicySetHelper.getPolicyIdReferencesValues(targetPolicySet);
        
        PolicyType targetPolicy = null;
        
        for (String policyId : policyIdList) {
            PolicyType policy = papContainer.getPolicy(policyId);
            
            if (actionValue.equals(PolicyWizard.getActionValue(policy))) {
                if (targetPolicy != null) {
                    throw new HighLevelPolicyManagementServiceException("More than one action with the same value");
                }
                targetPolicy = policy;
            }
        }
        
        String actionId;
        
        if (targetPolicy == null) {
            actionId = createAction(papContainer, targetPolicySet.getPolicySetId(), actionValue, bottom);
        } else {
            actionId = targetPolicy.getPolicyId();
        }
        
        return actionId;
    }

    /**
     * Create a new resource with the given value.
     * 
     * @param papContainer
     * @param resourceValue
     * @param bottom
     * @return the resource id.
     */
    private static String createResource(PapContainer papContainer, String resourceValue, boolean bottom) {

        PolicySetType resource = (new PolicySetWizard(new AttributeWizard("resource", resourceValue))).getXACML();

        String resourceId = resource.getPolicySetId();

        papContainer.storePolicySet(resource);

        PolicySetType rootPolicySet = papContainer.getRootPolicySet();

        int index = 0;

        if (bottom) {
            index = -1;
        }

        PolicySetHelper.addPolicySetReference(rootPolicySet, index, resourceId);

        papContainer.updatePolicySet(rootPolicySet);

        return resourceId;
    }

    /**
     * Create a new action with the given value inside the given resource.
     * 
     * @param papContainer
     * @param resourceId
     * @param actionValue
     * @param bottom
     * @return the id of the created action.
     */
    private static String createAction(PapContainer papContainer, String resourceId, String actionValue,
            boolean bottom) {

        PolicyType action = (new PolicyWizard(new AttributeWizard("action", actionValue))).getXACML();
        
        papContainer.storePolicy(action);
        
        String actionId = action.getPolicyId();

        int index = 0;

        if (bottom) {
            index = -1;
        }
        
        PolicySetType resource = papContainer.getPolicySet(resourceId);
        
        PolicySetHelper.addPolicyReference(resource, index, actionId);
        
        papContainer.updatePolicySet(resource);
        
        return actionId;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }
}
