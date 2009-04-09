package org.glite.authz.pap.authz.operations.highlevelpolicymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicyHelper;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.RuleWizard;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;

public class AddRuleOperation extends BasePAPOperation<String> {

    private String actionId;
    private String alias;
    private List<AttributeWizard> attributeWizardList;
    private EffectType effect;
    private boolean moveAfter;
    private String ruleId;

    protected AddRuleOperation(String alias, boolean isPermit, List<AttributeWizard> attributeWizardList,
            String actionId, String ruleId, boolean moveAfter) {

        this.alias = alias;
        this.attributeWizardList = attributeWizardList;
        this.actionId = actionId;
        this.ruleId = ruleId;
        this.moveAfter = moveAfter;

        if (isPermit) {
            effect = EffectType.Permit;
        } else {
            effect = EffectType.Deny;
        }

    }

    public static AddRuleOperation instance(String alias, boolean isPermit,
            List<AttributeWizard> attributeWizardList, String actionId, String ruleId, boolean moveAfter) {
        return new AddRuleOperation(alias, isPermit, attributeWizardList, actionId, ruleId, moveAfter);
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

            index = PolicyHelper.indexOfRule(policy, ruleId);

            if (index == -1) {
                throw new XACMLPolicyManagementServiceException("ruleId not found: " + ruleId);
            }

            if (moveAfter) {
                index++;
            }
        }

        RuleWizard ruleWizard = new RuleWizard(attributeWizardList, effect);

        PolicyHelper.addRule(policy, index, ruleWizard.getXACML());
        
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
}
