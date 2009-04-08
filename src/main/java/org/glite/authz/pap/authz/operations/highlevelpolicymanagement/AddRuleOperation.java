package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.RuleWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;

public class AddRuleOperation extends BasePAPOperation<String> {

    private String actionId;
    private String alias;
    private AttributeWizard banAttributeWizard;
    private EffectType effect;
    private boolean moveAfter;
    private String ruleId;

    protected AddRuleOperation(String alias, boolean isPermit, AttributeWizard banAttributeWizard,
            String actionId, String ruleId, boolean moveAfter) {

        this.alias = alias;
        this.banAttributeWizard = banAttributeWizard;
        this.actionId = actionId;
        this.ruleId = ruleId;
        this.moveAfter = moveAfter;

        if (isPermit) {
            effect = EffectType.Permit;
        } else {
            effect = EffectType.Deny;
        }

    }

    public static AddRuleOperation instance(String alias, boolean isPermit, AttributeWizard banAttributeWizard,
            String actionId, String ruleId, boolean moveAfter) {
        return new AddRuleOperation(alias,
                                    isPermit,
                                    banAttributeWizard,
                                    actionId,
                                    ruleId,
                                    moveAfter);
    }

    protected String doExecute() {

        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }

        Pap pap = PapManager.getInstance().getPap(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        PolicyType policy = papContainer.getPolicy(actionId);

        int index = 0;

        if (ruleId != null) {

            index = getRuleIndex(policy, ruleId);

            if (index == -1) {
                throw new XACMLPolicyManagementServiceException("ruleId not found: " + ruleId);
            }
            
            if (moveAfter) {
                index++;
            }
        }

        RuleWizard ruleWizard = new RuleWizard(banAttributeWizard, effect);

        // Because of a bug in OpenSAML it's not possible to simply add a rule by index.
        // The workaround uses a local list.
        List<RuleType> ruleList = policy.getRules();
        List<RuleType> savedRuleList = new LinkedList<RuleType>(ruleList);
        savedRuleList.add(index, ruleWizard.getXACML());
        
        ruleList.clear();
        
        for (RuleType rule : savedRuleList) {
            ruleList.add(rule);
        }
        // end of the workaround
        
        String version = policy.getVersion();

        PolicyWizard.increaseVersion(policy);

        TypeStringUtils.releaseUnneededMemory(policy);

        papContainer.updatePolicy(version, policy);
        
        return ruleWizard.getRuleId();
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }

    private int getRuleIndex(PolicyType policy, String ruleId) {

        List<RuleType> ruleList = policy.getRules();

        for (int i = 0; i < ruleList.size(); i++) {

            RuleType rule = ruleList.get(i);

            if (ruleId.equals(rule.getRuleId())) {
                return i;
            }
        }
        return -1;
    }
}
