package org.glite.authz.pap.services;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.glite.authz.pap.ui.wizard.AttributeWizard;
import org.glite.authz.pap.ui.wizard.BlacklistPolicySet;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicySet;
import org.glite.authz.pap.ui.wizard.AttributeWizard.AttributeWizardType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelPolicyManagementService implements HighLevelPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(HighLevelPolicyManagementService.class);

    public String banDN(String dn, boolean isPublic) throws RemoteException {

        log.info("Received banDN() request (dn=" + dn + ", isPublic=" + isPublic);

        String policyId = null;

        try {
            policyId = banAttribute(AttributeWizardType.DN, dn, isPublic);
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.logAndThrow(log, e);
        }

        log.info("Created BlackList policy: " + policyId);

        return policyId;

    }

    public String banFQAN(String fqan, boolean isPublic) throws RemoteException {

        log.info("Received banFQAN() request (fqan=" + fqan + ", isPublic=" + isPublic);

        String policyId = null;

        try {
            policyId = banAttribute(AttributeWizardType.FQAN, fqan, isPublic);
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.logAndThrow(log, e);
        }

        log.info("Created BlackList policy: " + policyId);

        return policyId;

    }

    public String dnJobPriority(String dn, String serviceClass, boolean isPublic) throws RemoteException {
        log.info("Received dnJobPriority() request (dn=" + dn + ", serviceClass=" + serviceClass + ", isPublic=" + isPublic);

        String policyId = null;

        try {
            policyId = jobPriorityPolicy(AttributeWizardType.DN, dn, serviceClass, isPublic);
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.logAndThrow(log, e);
        }

        log.info("Created ServiceClass policy: " + policyId);

        return policyId;
    }

    public String fqanJobPriority(String fqan, String serviceClass, boolean isPublic) throws RemoteException {
        log
                .info("Received fqanJobPriority() request (fqan=" + fqan + ", serviceClass=" + serviceClass + ", isPublic="
                        + isPublic);

        String policyId = null;

        try {
            policyId = jobPriorityPolicy(AttributeWizardType.FQAN, fqan, serviceClass, isPublic);
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.logAndThrow(log, e);
        }

        log.info("Created ServiceClass policy: " + policyId);

        return policyId;
    }

    public UnbanResult unbanDN(String dn) throws RemoteException {
        log.info("Received unbanDN() request (dn=" + dn + ")");

        UnbanResult unbanResult = null;

        try {

            unbanResult = unbanAttribute(AttributeWizardType.DN, dn);
            if (unbanResult.getStatusCode() == 0)
                log.info("dn=\"" + dn + "\" successfully unbanned");
            else
                log.info("dn=\"" + dn + "\" NOT unbanned");

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.logAndThrow(log, e);
        }
        return unbanResult;
    }

    public UnbanResult unbanFQAN(String fqan) throws RemoteException {

        log.info("Received unbanFQAN() request (fqan=" + fqan + ")");

        UnbanResult unbanResult = null;

        try {

            unbanResult = unbanAttribute(AttributeWizardType.FQAN, fqan);

            if (unbanResult.getStatusCode() == 0)
                log.info("fqan=\"" + fqan + "\" successfully unbanned");
            else
                log.info("fqan" + fqan + "\" NOT unbanned");

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.logAndThrow(log, e);
        }

        return unbanResult;

    }

    private String banAttribute(AttributeWizardType attributeWizardType, String attributeValue, boolean isPublic) {

        List<AttributeWizard> targetList = new LinkedList<AttributeWizard>();
        targetList.add(new AttributeWizard(attributeWizardType, attributeValue));
        targetList.add(new AttributeWizard(AttributeWizardType.RESOURCE_URI, "*"));

        PolicyWizard policyWizard = new PolicyWizard(targetList, null, EffectType.Deny);

        if (isPublic)
            policyWizard.setPrivate(false);
        else
            policyWizard.setPrivate(true);

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        localPAP.addPolicy(BlacklistPolicySet.POLICY_SET_ID, policyWizard.getPolicyType());

        return policyWizard.getPolicyType().getPolicyId();

    }

    private String jobPriorityPolicy(AttributeWizardType attributeWizardType, String attributeValue, String serviceClass,
            boolean isPublic) {

        List<AttributeWizard> targetList = new LinkedList<AttributeWizard>();
        targetList.add(new AttributeWizard(attributeWizardType, attributeValue));
        targetList.add(new AttributeWizard(AttributeWizardType.RESOURCE_URI, "*"));
        targetList.add(new AttributeWizard(AttributeWizardType.SERVICE_CLASS, serviceClass));

        PolicyWizard policyWizard = new PolicyWizard(targetList, null, EffectType.Permit);

        if (isPublic)
            policyWizard.setPrivate(false);
        else
            policyWizard.setPrivate(true);

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        localPAP.addPolicy(ServiceClassPolicySet.POLICY_SET_ID, policyWizard.getPolicyType());

        return policyWizard.getPolicyType().getPolicyId();

    }

    private UnbanResult unbanAttribute(AttributeWizardType attributeType, String attributeValue) {

        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();

        List<PolicyType> policyList = localPAP.getAllPolicies();

        boolean noPoliciesRemoved = true;

        for (PolicyType policy : policyList) {

            PolicyWizard policyWizard = new PolicyWizard(policy);

            if (AttributeWizardType.DN.equals(attributeType)) {

                if (policyWizard.isBanPolicyForDN(attributeValue)) {
                    localPAP.removePolicyAndReferences(policy.getPolicyId());
                    noPoliciesRemoved = false;
                }
            } else {

                if (policyWizard.isBanPolicyForFQAN(attributeValue)) {
                    localPAP.removePolicyAndReferences(policy.getPolicyId());
                    noPoliciesRemoved = false;
                }
            }
        }

        UnbanResult unbanResult = new UnbanResult();
        unbanResult.setConflictingPolicies(new String[0]);

        if (noPoliciesRemoved)
            unbanResult.setStatusCode(1);
        else
            unbanResult.setStatusCode(0);

        return unbanResult;
    }
}
