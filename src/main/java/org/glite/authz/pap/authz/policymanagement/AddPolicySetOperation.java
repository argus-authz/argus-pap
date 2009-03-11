package org.glite.authz.pap.authz.policymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.WizardUtils;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.opensaml.xacml.policy.PolicySetType;

public class AddPolicySetOperation extends BasePAPOperation<String> {

	int index;
    PolicySetType policySet;

    protected AddPolicySetOperation(int index, PolicySetType policySet) {
    	this.index = index;
        this.policySet = policySet;
    }

    public static AddPolicySetOperation instance(int index, PolicySetType policySet) {
        return new AddPolicySetOperation(index, policySet);
    }

    protected String doExecute() {

        String policySetId = WizardUtils.generateId(null);
        
        policySet.setPolicySetId(policySetId);

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        localPAP.addPolicySet(index, policySet);
        
        TypeStringUtils.releaseUnnecessaryMemory(policySet);
        
        log.info(String.format("Added policy (policyId=\"%s\")", policySetId));

        return policySetId;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
