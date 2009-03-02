package org.glite.authz.pap.authz.highlevelpolicymanagement;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.wizard.JobPriorityPolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.ServiceClassPolicySet;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;

public class DNJobPriorityOperation extends BasePAPOperation<String> {

    String dn;
    String serviceClass;
    String description;
    boolean isPublic;

    protected DNJobPriorityOperation(String dn, String serviceClass, boolean isPublic, String description) {

        this.dn = dn;
        this.serviceClass = serviceClass;;
        this.description = description;
        this.isPublic = isPublic;
    }

    public static DNJobPriorityOperation instance(String dn, String serviceClass, boolean isPublic, String description) {
        return new DNJobPriorityOperation(dn, serviceClass, isPublic, description);
    }

    protected String doExecute() {

        PolicyWizard policyWizard = JobPriorityPolicyWizard.getPolicyWizard(AttributeWizardType.DN, dn, serviceClass, isPublic,
                description);

        String policyId = policyWizard.getPolicyType().getPolicyId();

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        localPAP.addPolicy(ServiceClassPolicySet.POLICY_SET_ID, policyWizard.getPolicyType());

        log.info("Added ServiceClass policy: " + policyId);

        return policyId;
    }

    @Override
    protected void setupPermissions() {

        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));

    }

}
