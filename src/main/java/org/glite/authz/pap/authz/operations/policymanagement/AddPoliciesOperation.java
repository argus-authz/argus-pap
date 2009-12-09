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
import org.opensaml.xacml.policy.PolicyType;

public class AddPoliciesOperation extends BasePAPOperation<String[]> {

    int index;
    String alias;
    PolicyType[] policyArray;
    String[] policyIdPrefix;
    String policySetId;

    protected AddPoliciesOperation(String alias, int index, String policySetId, String[] policyIdPrefix, PolicyType[] policyArray) {
        this.alias = alias;
        this.index = index;
        this.policySetId = policySetId;
        this.policyIdPrefix = policyIdPrefix;
        this.policyArray = policyArray;
    }

    public static AddPoliciesOperation instance(String alias, int index, String policySetId, String policyIdPrefix[], PolicyType[] policyArray) {
        return new AddPoliciesOperation(alias, index, policySetId, policyIdPrefix, policyArray);
    }

    protected String[] doExecute() {
        
        if (alias == null) {
            alias = Pap.DEFAULT_PAP_ALIAS;
        }
        
        Pap pap = PapManager.getInstance().getPap(alias);
        
        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PapContainer papContainer = new PapContainer(pap);

        if (!papContainer.hasPolicySet(policySetId)) {
            log.warn(String.format("Policies not added because PolicySetId \"%s\" does not exists.", policySetId));
            return null;
        }

        String[] policyIdArray = new String[policyArray.length];

        for (int i = 0; i < policyArray.length; i++) {

            policyIdArray[i] = WizardUtils.generateId(policyIdPrefix[i]);
            
            policyArray[i].setPolicyId(policyIdArray[i]);

            if (index == -1) {
                papContainer.addPolicy(index, policySetId, policyArray[i]);
            } else {
                papContainer.addPolicy(index + i, policySetId, policyArray[i]);
            }
            TypeStringUtils.releaseUnneededMemory(policyArray[i]);

            log.info(String.format("Added policy (policyId=\"%s\")", policyIdArray[i]));
        }
        return policyIdArray;
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }
}
