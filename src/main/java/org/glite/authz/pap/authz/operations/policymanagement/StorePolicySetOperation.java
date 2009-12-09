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

public class StorePolicySetOperation extends BasePAPOperation<String> {

    String alias;
    String idPrefix;
    PolicySetType policySet;

    private StorePolicySetOperation(String alias, String idPrefix, PolicySetType policySet) {

        this.alias = alias;
        this.idPrefix = idPrefix;
        this.policySet = policySet;

    }

    public static StorePolicySetOperation instance(String alias, String idPrefix, PolicySetType policySet) {

        return new StorePolicySetOperation(alias, idPrefix, policySet);
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

        String policySetId = WizardUtils.generateId(idPrefix); 
    	policySet.setPolicySetId(policySetId);

        papContainer.storePolicySet(policySet);

        TypeStringUtils.releaseUnneededMemory(policySet);

        return policySetId;
    }

    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
