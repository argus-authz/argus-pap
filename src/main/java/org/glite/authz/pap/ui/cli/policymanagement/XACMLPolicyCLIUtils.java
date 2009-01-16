package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.glite.authz.pap.ui.wizard.BlacklistPolicySet;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicySet;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xml.ConfigurationException;

public class XACMLPolicyCLIUtils {

    public synchronized static void addPolicy(PolicyWizard policy, XACMLPolicyManagement policyMgmtClient) throws RemoteException {

        PolicySetType policySet;

        if (policy.isBlacklistPolicy())
            policySet = (new BlacklistPolicySet()).getPolicySetType();
        else
            policySet = (new ServiceClassPolicySet()).getPolicySetType();

        String policySetId = policySet.getPolicySetId();

        boolean updateOperation = false;

        if (policyMgmtClient.hasPolicySet(policySetId)) {
            updateOperation = true;
            policySet = policyMgmtClient.getPolicySet(policySetId);
        }

        String policyId = policyMgmtClient.storePolicy(policy.getPolicyIdPrefix(), policy.getPolicyType());

        PolicySetHelper.addPolicyReference(policySet, policyId);

        if (updateOperation)
            policyMgmtClient.updatePolicySet(policySet);
        else {
            policyMgmtClient.storePolicySet(null, policySet);

            PolicySetType localPolicySet = policyMgmtClient.getPolicySet(PAP.localPAPId);

            PolicySetHelper.addPolicySetReference(localPolicySet, policySetId);

            policyMgmtClient.updatePolicySet(localPolicySet);
        }

        // the effective PolicyId is the one returned by the server
        policy.getPolicyType().setPolicyId(policyId);

    }

    public static void initOpenSAML() {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            throw new PAPConfigurationException("Error initializing OpenSAML library", e);
        }
    }
    
    public static void removePolicy(PolicyWizard policy, XACMLPolicyManagement policyMgmtClient) throws NotFoundException,
            RepositoryException, RemoteException {

        PolicySetType policySet;
        String policySetId;

        if (policy.isBlacklistPolicy())
            policySetId = BlacklistPolicySet.POLICY_SET_ID;
        else
            policySetId = ServiceClassPolicySet.POLICY_SET_ID;

        policySet = policyMgmtClient.getPolicySet(policySetId);

        String policyId = policy.getPolicyType().getPolicyId();
        PolicySetHelper.deletePolicyReference(policySet, policyId);

        policyMgmtClient.updatePolicySet(policySet);

        policyMgmtClient.removePolicy(policyId);

    }

}
