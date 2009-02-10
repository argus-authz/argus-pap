package org.glite.authz.pap.services;

import java.rmi.RemoteException;
import java.util.List;

import org.glite.authz.pap.authz.highlevelpolicymanagement.BanDNOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.BanFQANOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.DNJobPriorityOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.FQANJobPriorityOperation;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelPolicyManagementService implements HighLevelPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(HighLevelPolicyManagementService.class);

    public String banDN(String dn, boolean isPublic, String description) throws RemoteException {
        log.info(String.format("Received banDN(dn=\"%s\", isPublic=%s);", dn, String.valueOf(isPublic)));

        try {

            return BanDNOperation.instance(dn, isPublic, description).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String banFQAN(String fqan, boolean isPublic, String description) throws RemoteException {
        log.info(String.format("Received banFQAN(fqan=\"%s\", isPublic=%s);", fqan, String.valueOf(isPublic)));

        try {

            return BanFQANOperation.instance(fqan, isPublic, description).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String dnJobPriority(String dn, String serviceClass, boolean isPublic, String description) throws RemoteException {

        log.info(String.format("Received dnJobPriority(dn=\"%s\", serviceClass=\"%s\", isPublic=%s);", dn, serviceClass, String
                .valueOf(isPublic)));
        try {

            return DNJobPriorityOperation.instance(dn, serviceClass, isPublic, description).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String fqanJobPriority(String fqan, String serviceClass, boolean isPublic, String description) throws RemoteException {

        log.info(String.format("Received fqanJobPriority(fqan=\"%s\", serviceClass=\"%s\", isPublic=%s);", fqan, serviceClass, String
                .valueOf(isPublic)));

        try {

            return FQANJobPriorityOperation.instance(fqan, serviceClass, isPublic, description).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public UnbanResult unbanDN(String dn) throws RemoteException {
        log.info("Received unbanDN() request (dn=" + dn + ")");

        try {

            UnbanResult unbanResult = null;
            unbanResult = unbanAttribute(AttributeWizardType.DN, dn);
            if (unbanResult.getStatusCode() == 0)
                log.info("dn=\"" + dn + "\" successfully unbanned");
            else
                log.info("dn=\"" + dn + "\" NOT unbanned");
            return unbanResult;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public UnbanResult unbanFQAN(String fqan) throws RemoteException {

        log.info("Received unbanFQAN() request (fqan=" + fqan + ")");

        try {

            UnbanResult unbanResult = null;
            unbanResult = unbanAttribute(AttributeWizardType.FQAN, fqan);
            if (unbanResult.getStatusCode() == 0)
                log.info("fqan=\"" + fqan + "\" successfully unbanned");
            else
                log.info("fqan" + fqan + "\" NOT unbanned");
            return unbanResult;

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }

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
