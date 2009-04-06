package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicyType;

public class AddPolicyOperation extends BasePAPOperation<String> {

    String alias;
    int index;
    PolicyType policy;
    String policyIdPrefix;
    String policySetId;

    protected AddPolicyOperation(String alias, int index, String policySetId, String policyIdPrefix, PolicyType policy) {
        this.alias = alias;
        this.index = index;
        this.policySetId = policySetId;
        this.policyIdPrefix = policyIdPrefix;
        this.policy = policy;
    }

    public static AddPolicyOperation instance(String alias, int index, String policySetId, String policyIdPrefix,
            PolicyType policy) {
        return new AddPolicyOperation(alias, index, policySetId, policyIdPrefix, policy);
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

        if (!papContainer.hasPolicySet(policySetId)) {
            log.warn(String.format("Policy not added because PolicySetId \"%s\" does not exists.", policySetId));
            return null;
        }

        String policyId = WizardUtils.generateId(policyIdPrefix);

        policy.setPolicyId(policyId);

        papContainer.addPolicy(index, policySetId, policy);

        TypeStringUtils.releaseUnneededMemory(policy);

        log.info(String.format("Added policy (policyId=\"%s\")", policyId));

        return policyId;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
