/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.ObligationsHelper;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.ObligationWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.RuleWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.HighLevelPolicyManagementServiceException;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class AddRuleOperation extends BasePAPOperation<String> {
	
	public enum ObligationScopeType{
		action,
		resource
	};

    private String actionId;
    private String actionValue;
    private String resourceValue;
    private String obligationValue;
    private ObligationScopeType obligationScope;
    
    private String alias;
    private List<AttributeWizard> attributeWizardList;
    private EffectType effect;
    private boolean after;
    private String ruleId;
    

    protected AddRuleOperation(String alias, boolean isPermit, List<AttributeWizard> attributeWizardList,
            String actionValue, String resourceValue, String actionId, String ruleId, String obligationValue, 
            String obligationScope, boolean after) {

        this.alias = alias;
        this.attributeWizardList = attributeWizardList;
        this.actionId = actionId;
        this.actionValue = actionValue;
        this.resourceValue = resourceValue;
        this.obligationValue = obligationValue;
        
        this.obligationScope = ObligationScopeType.valueOf(obligationScope);
        
        this.ruleId = ruleId;
        
        this.after = after;

        if (isPermit) {
            effect = EffectType.Permit;
        } else {
            effect = EffectType.Deny;
        }

    }
    
    
    protected PolicySetType findResourcePolicySet(PapContainer pc){
    	
    	List<PolicySetType> policySetList = pc.getAllPolicySets();

        PolicySetType targetPolicySet = null;
        
        // Skipping the first policy set as it is the root policy set.
        for (int i = 1; i < policySetList.size(); i++) {
            PolicySetType policySet = policySetList.get(i);
            if (resourceValue.equals(PolicySetWizard.getResourceValue(policySet))) {
                if (targetPolicySet != null) {
                    throw new HighLevelPolicyManagementServiceException("More than one resource policy sets match the given resource id!");
                }
                targetPolicySet = policySet;
            }
        }
        
        policySetList = null;
    	
    	return targetPolicySet;
    	
    }

    public static AddRuleOperation instance(String alias, boolean isPermit,
            List<AttributeWizard> attributeWizardList, String actionValue, String resourceValue,
            String actionId, String ruleId, String obligationId, String obligationScope, boolean after) {
        return new AddRuleOperation(alias,
                                    isPermit,
                                    attributeWizardList,
                                    actionValue,
                                    resourceValue,
                                    actionId,
                                    ruleId,
                                    obligationId,
                                    obligationScope,
                                    after);
    }

    protected String doExecute() {

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }

        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("You cannot add rules to a remote PAP!");
        }
        
        if ((actionId == null) && (actionValue == null)) {
            throw new XACMLPolicyManagementServiceException("Action id and action value are both unspecified.");
        }
        
        RuleWizard ruleWizard = new RuleWizard(attributeWizardList, effect);
        
        PapContainer papContainer = new PapContainer(pap);
        
        if (actionId == null) {
            actionId = getActionId(papContainer, actionValue, resourceValue, after);
        }
        
        PolicyType actionPolicy = papContainer.getPolicy(actionId);
        
        
        if (obligationValue!= null){
        	if (ObligationScopeType.action.equals(obligationScope)){
        		
        		if (!PolicyHelper.hasObligationWithId(actionPolicy, obligationValue)){
        			ObligationWizard owiz = new ObligationWizard(obligationValue);
        			ObligationsType obligations = ObligationsHelper.build();
        			obligations.getObligations().add(owiz.getXACML());
        			actionPolicy.setObligations(obligations);
        		}
        		
        	}else{
        		
        		PolicySetType resourcePolicySet = findResourcePolicySet(papContainer);
        		if (!PolicySetHelper.hasObligationWithId(resourcePolicySet, obligationValue)){
        			ObligationWizard owiz = new ObligationWizard(obligationValue);
        			ObligationsType obligations = ObligationsHelper.build();
        			obligations.getObligations().add(owiz.getXACML());
        			resourcePolicySet.setObligations(obligations);
        		}
        		
        	}
        }
        
        int index = 0;

        if (ruleId != null) {

            index = PolicyHelper.indexOfRule(actionPolicy, ruleId);

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

        
        PolicyHelper.addRule(actionPolicy, index, ruleWizard.getXACML());

        String version = actionPolicy.getVersion();

        PolicyWizard.increaseVersion(actionPolicy);
        

        TypeStringUtils.releaseUnneededMemory(actionPolicy);

        papContainer.updatePolicy(version, actionPolicy);

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

        
        PolicySetType targetPolicySet = findResourcePolicySet(papContainer);
        
        if (targetPolicySet == null) {
            String resourceId = createResource(papContainer, resourceValue, bottom);
            return createAction(papContainer, resourceId, actionValue, bottom);
        }
        
                
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
    private String createResource(PapContainer papContainer, String resourceValue, boolean bottom) {

    	PolicySetWizard psWizard = new PolicySetWizard(new AttributeWizard("resource", resourceValue));
    	
    	if (obligationValue!= null && obligationScope.equals(ObligationScopeType.resource))
    		psWizard.addObligation(new ObligationWizard(obligationValue));	
    	
        PolicySetType resource = psWizard.getXACML();

        String resourceId = resource.getPolicySetId();

        papContainer.storePolicySet(resource);

        PolicySetType rootPolicySet = papContainer.getRootPolicySet();

        int index = 0;

        if (bottom) {
            index = -1;
        }

        PolicySetHelper.addPolicySetReference(rootPolicySet, index, resourceId);

        String version = rootPolicySet.getVersion();
        PolicySetWizard.increaseVersion(rootPolicySet);

        papContainer.updatePolicySet(version, rootPolicySet);

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
    private String createAction(PapContainer papContainer, String resourceId, String actionValue,
            boolean bottom) {

    	PolicyWizard pw = new PolicyWizard(new AttributeWizard("action", actionValue));
    	
    	if (obligationValue != null && obligationScope.equals(ObligationScopeType.action)){
    		pw.addObligation(new ObligationWizard(obligationValue));
    	}
    	
        PolicyType action = pw.getXACML();
        
        papContainer.storePolicy(action);
        
        String actionId = action.getPolicyId();

        int index = 0;

        if (bottom) {
            index = -1;
        }
        
        PolicySetType resource = papContainer.getPolicySet(resourceId);
        
        PolicySetHelper.addPolicyReference(resource, index, actionId);
        
        String version = resource.getVersion();
        PolicySetWizard.increaseVersion(resource);
        
        papContainer.updatePolicySet(version, resource);
        
        return actionId;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }
}
