package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicyType;

public class StorePolicyOperation extends BasePAPOperation<String> {

    String alias;
    String idPrefix;
    PolicyType policy;

    protected StorePolicyOperation(String alias, String idPrefix, PolicyType policy) {

        this.alias = alias;
        this.idPrefix = idPrefix;
        this.policy = policy;
    }

    public static StorePolicyOperation instance(String alias, String idPrefix, PolicyType policy) {

        return new StorePolicyOperation(alias, idPrefix, policy);
    }

    protected String doExecute() {

        if (alias == null) {
            alias = PAP.DEFAULT_PAP_ALIAS;
        }
        
        PAP pap = PapManager.getInstance().getPAP(alias);

        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        String policyId = WizardUtils.generateId(idPrefix);
        policy.setPolicyId(policyId);

        papContainer.storePolicy(policy);

        TypeStringUtils.releaseUnneededMemory(policy);

        return policyId;

    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
