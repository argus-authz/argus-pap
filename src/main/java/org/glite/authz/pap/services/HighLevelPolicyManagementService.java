package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.highlevelpolicymanagement.BanOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.EraseRepositoryOperation;
import org.glite.authz.pap.authz.highlevelpolicymanagement.UnbanOperation;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard.AttributeWizardType;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelPolicyManagementService implements HighLevelPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(HighLevelPolicyManagementService.class);

    public String ban(String alias, String id, String value, String resource, String action, boolean isPublic) throws RemoteException {
        log.info(String.format("Received ban(id=\"%s\" value=\"%s\", resource=\"%s\", action=\"%s\", isPublic=%s);",
        					   id,
                               value,
                               resource,
                               action,
                               String.valueOf(isPublic)));
        try {

            AttributeWizard banAttributeWizard = new AttributeWizard(id, value);
            AttributeWizard resourceAttributeWizard = new AttributeWizard(AttributeWizardType.RESOURCE_PS, resource);
            AttributeWizard actionAttributeWizard = new AttributeWizard(AttributeWizardType.ACTION, action);

            synchronized (PAPContainer.addOperationLock) {
				return BanOperation.instance(alias, banAttributeWizard, resourceAttributeWizard,
					actionAttributeWizard, isPublic).execute();
			}

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }
    
    public void eraseRepository(String alias) throws RemoteException {
        log.info("Received eraseRepository();");

        try {

            EraseRepositoryOperation.instance(alias).execute();

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public UnbanResult unban(String alias, String id, String value, String resource, String action) throws RemoteException {
        log.info(String.format("Received unban(id=\"%s\" value=\"%s\", resource=\"%s\", action=\"%s\");",
        					   id,
                               value,
                               resource,
                               action));
        try {

            AttributeWizard bannedAttributeWizard = new AttributeWizard(id, value);
            AttributeWizard resourceAttributeWizard = new AttributeWizard(AttributeWizardType.RESOURCE_PS, resource);
            AttributeWizard actionAttributeWizard = new AttributeWizard(AttributeWizardType.ACTION, action);

            synchronized (PAPContainer.addOperationLock) {
				return UnbanOperation.instance(alias, bannedAttributeWizard, resourceAttributeWizard,
					actionAttributeWizard).execute();
			}

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }
}
