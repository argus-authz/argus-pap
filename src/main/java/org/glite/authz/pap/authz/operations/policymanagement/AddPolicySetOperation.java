package org.glite.authz.pap.authz.operations.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.services.exceptions.XACMLPolicyManagementServiceException;
import org.opensaml.xacml.policy.PolicySetType;

public class AddPolicySetOperation extends BasePAPOperation<String> {

    String alias;
    int index;
    PolicySetType policySet;

    protected AddPolicySetOperation(String alias, int index, PolicySetType policySet) {
        this.alias = alias;
        this.index = index;
        this.policySet = policySet;
    }

    public static AddPolicySetOperation instance(String alias, int index, PolicySetType policySet) {
        return new AddPolicySetOperation(alias, index, policySet);
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

        String policySetId = WizardUtils.generateId(null);

        policySet.setPolicySetId(policySetId);

        papContainer.addPolicySet(index, policySet);

        TypeStringUtils.releaseUnneededMemory(policySet);

        log.info(String.format("Added policy set (policyId=\"%s\")", policySetId));

        return policySetId;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
