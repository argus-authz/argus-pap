package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.XACMLPolicyManagementServiceException;
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
            alias = PAP.DEFAULT_PAP_ALIAS;
        }
        
        PAP pap = PAPManager.getInstance().getPAP(alias);
        
        if (pap.isRemote()) {
            throw new XACMLPolicyManagementServiceException("Forbidden operation for a remote PAP");
        }

        PAPContainer papContainer = new PAPContainer(pap);

        if (!papContainer.hasPolicySet(policySetId)) {
            log.warn(String.format("Policy not added because PolicySetId \"%s\" does not exists.", policySetId));
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
