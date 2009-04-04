package org.glite.authz.pap.services;

import java.rmi.RemoteException;

import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.AddRuleOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.BanOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.EraseRepositoryOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.PurgeOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.UnbanOperation;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizardTypeConfiguration;
import org.glite.authz.pap.papmanagement.PAPContainer;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelPolicyManagementService implements HighLevelPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(HighLevelPolicyManagementService.class);

    public String addRule(String alias, boolean isPermit, String id, String value, String actionId,
            String ruleId, boolean moveAfter) throws RemoteException {
        log.info(String.format("Received addRule(isPermit=%b, id=\"%s\", value=\"%s\", actionId=\"%s\", ruleId=\"%s\", moveAfter=%b);",
                               isPermit,
                               id,
                               value,
                               actionId,
                               ruleId,
                               moveAfter));
        try {
            synchronized (PAPContainer.highLevelOperationLock) {
                return AddRuleOperation.instance(alias,
                                                 isPermit,
                                                 new AttributeWizard(id, value),
                                                 actionId,
                                                 ruleId,
                                                 moveAfter).execute();
            }
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }

    }

    public String ban(String alias, String id, String value, String resource, String action, boolean isPublic)
            throws RemoteException {
        log.info(String.format("Received ban(id=\"%s\" value=\"%s\", resource=\"%s\", action=\"%s\", isPublic=%b);",
                               id,
                               value,
                               resource,
                               action,
                               isPublic));
        try {
            AttributeWizard banAttributeWizard = new AttributeWizard(id, value);
            AttributeWizard resourceAttributeWizard = new AttributeWizard(AttributeWizardTypeConfiguration.getInstance()
                                                                                                          .getResourceAttributeWizard(),
                                                                          resource);
            AttributeWizard actionAttributeWizard = new AttributeWizard(AttributeWizardTypeConfiguration.getInstance()
                                                                                                        .getActionAttributeWizard(),
                                                                        action);
            synchronized (PAPContainer.highLevelOperationLock) {
                return BanOperation.instance(alias,
                                             banAttributeWizard,
                                             resourceAttributeWizard,
                                             actionAttributeWizard,
                                             isPublic).execute();
            }
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public void eraseRepository(String alias) throws RemoteException {
        log.info("Received eraseRepository();");

        try {

            synchronized (PAPContainer.highLevelOperationLock) {
                EraseRepositoryOperation.instance(alias).execute();
            }

        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public void purge(String alias, boolean purgeUnreferencedPolicies, boolean purgeEmptyPolicies,
            boolean purgeUnreferencedPolicySets, boolean purgeEmptyPolicySets) throws RemoteException {
        log.info(String.format("Received unban(alias=%s purgeUnreferencedPolicies=%b purgeEmptyPolicies=%b, purgeUnreferencedPolicySets=%b, purgeEmptyPolicySets=%b);",
                               alias,
                               purgeUnreferencedPolicies,
                               purgeEmptyPolicies,
                               purgeUnreferencedPolicySets,
                               purgeEmptyPolicySets));
        try {
            synchronized (PAPContainer.highLevelOperationLock) {
                PurgeOperation.instance(alias,
                                        purgeUnreferencedPolicies,
                                        purgeEmptyPolicies,
                                        purgeUnreferencedPolicySets,
                                        purgeEmptyPolicySets).execute();
            }
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }

    public UnbanResult unban(String alias, String id, String value, String resource, String action)
            throws RemoteException {
        log.info(String.format("Received unban(alias=%s id=%s value=%s, resource=%s, action=%s);",
                               alias,
                               id,
                               value,
                               resource,
                               action));
        try {

            AttributeWizard bannedAttributeWizard = new AttributeWizard(id, value);
            AttributeWizard resourceAttributeWizard = new AttributeWizard(AttributeWizardTypeConfiguration.getInstance()
                                                                                                          .getResourceAttributeWizard(),
                                                                          resource);
            AttributeWizard actionAttributeWizard = new AttributeWizard(AttributeWizardTypeConfiguration.getInstance()
                                                                                                        .getActionAttributeWizard(),
                                                                        action);
            synchronized (PAPContainer.highLevelOperationLock) {
                return UnbanOperation.instance(alias,
                                               bannedAttributeWizard,
                                               resourceAttributeWizard,
                                               actionAttributeWizard).execute();
            }
        } catch (RuntimeException e) {
            ServiceClassExceptionManager.log(log, e);
            throw e;
        }
    }
}
