/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.AddRuleOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.BanOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.EraseRepositoryOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.PurgeOperation;
import org.glite.authz.pap.authz.operations.highlevelpolicymanagement.UnbanOperation;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizard;
import org.glite.authz.pap.common.xacml.wizard.AttributeWizardTypeConfiguration;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.UnbanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelPolicyManagementService implements HighLevelPolicyManagement {

    private static final Logger log = LoggerFactory.getLogger(HighLevelPolicyManagementService.class);

    public String addRule(String alias, boolean isPermit, String[] attributeList, String actionValue,
            String resourceValue, String actionId, String ruleId, boolean moveAfter) throws RemoteException {
        log.info(String.format("Received addRule(isPermit=%b, ..., actionId=\"%s\", ruleId=\"%s\", moveAfter=%b);",
                               isPermit,
                               actionId,
                               ruleId,
                               moveAfter));
        try {
            synchronized (ServicesUtils.highLevelOperationLock) {
                List<AttributeWizard> attributeWizardList = new ArrayList<AttributeWizard>(attributeList.length);

                for (String attribute : attributeList) {
                    attributeWizardList.add(new AttributeWizard(attribute));
                }

                return AddRuleOperation.instance(alias,
                                                 isPermit,
                                                 attributeWizardList,
                                                 actionValue, 
                                                 resourceValue,
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
            synchronized (ServicesUtils.highLevelOperationLock) {
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

            synchronized (ServicesUtils.highLevelOperationLock) {
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
            synchronized (ServicesUtils.highLevelOperationLock) {
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
            synchronized (ServicesUtils.highLevelOperationLock) {
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
