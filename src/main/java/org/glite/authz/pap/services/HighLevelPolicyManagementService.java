package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.highlevelpolicymanagement.BanOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.DNJobPriorityOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.FQANJobPriorityOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.UnbanDNOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.UnbanFQANOperation;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelPolicyManagementService implements HighLevelPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(HighLevelPolicyManagementService.class);

    public String banDN(String dn, boolean isPublic, String description) throws RemoteException {
        log.info(String.format("Received banDN(dn=\"%s\", isPublic=%s);", dn, String.valueOf(isPublic)));

        try {
            
            AttributeWizard banAttributeWizard = new AttributeWizard(AttributeWizardType.DN, dn);
            AttributeWizard resourceAttributeWizard = new AttributeWizard(AttributeWizardType.RESOURCE_PS, "*");
            AttributeWizard actionAttributeWizard = new AttributeWizard(AttributeWizardType.ACTION, "*");
            
            return BanOperation.instance(banAttributeWizard, resourceAttributeWizard, actionAttributeWizard, isPublic, description).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public String banFQAN(String fqan, boolean isPublic, String description) throws RemoteException {
        log.info(String.format("Received banFQAN(fqan=\"%s\", isPublic=%s);", fqan, String.valueOf(isPublic)));

        try {

            AttributeWizard banAttributeWizard = new AttributeWizard(AttributeWizardType.FQAN, fqan);
            AttributeWizard resourceAttributeWizard = new AttributeWizard(AttributeWizardType.RESOURCE_PS, "*");
            AttributeWizard actionAttributeWizard = new AttributeWizard(AttributeWizardType.ACTION, "*");
            
            return BanOperation.instance(banAttributeWizard, resourceAttributeWizard, actionAttributeWizard, isPublic, description).execute();

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
        log.info(String.format("Received unbanDN(\"%s\");", dn));

        try {

            return UnbanDNOperation.instance(dn).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public UnbanResult unbanFQAN(String fqan) throws RemoteException {
        log.info(String.format("Received unbanFQAN(\"%s\");", fqan));

        try {

            return UnbanFQANOperation.instance(fqan).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }
}
