package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.common.PAP;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.common.utils.xacml.PolicySetHelper;
import org.glite.authz.pap.policymanagement.PolicyManagementService;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;
import org.glite.authz.pap.ui.wizard.BlacklistPolicySet;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.ServiceClassPolicySet;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLConfigurator;

public abstract class PolicyManagementCLI extends ServiceCLI {

    protected static final String SERVICE_NAME = "PolicyManagementService";

    protected PolicyManagementService policyMgmtClient;

    public PolicyManagementCLI(String[] commandNameValues, String usage, String description,
            String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }

    protected void addPolicy(PolicyWizard policy) throws CLIException, RemoteException {

        PolicySetType policySet;
        String policySetId;

        if (policy.isBlacklistPolicy())
            policySetId = BlacklistPolicySet.POLICY_SET_ID;
        else
            policySetId = ServiceClassPolicySet.POLICY_SET_ID;

        boolean updateOperation;

        try {
            policySet = policyMgmtClient.getPolicySet(policySetId);
            updateOperation = true;
        } catch (NotFoundException e) {

            if (policy.isBlacklistPolicy())
                policySet = (new BlacklistPolicySet()).getPolicySetType();
            else
                policySet = (new ServiceClassPolicySet()).getPolicySetType();

            updateOperation = false;
        }

        String policyId = policyMgmtClient.storePolicy(policy.getPolicyIdPrefix(), policy
                .getPolicyType());

        PolicySetHelper.addPolicyReference(policySet, policyId);

        if (updateOperation)
            policyMgmtClient.updatePolicySet(policySet);
        else
            policyMgmtClient.storePolicySet(null, policySet);

        try {
            policySet = policyMgmtClient.getPolicySet(PAP.localPAPId);
            
            PolicySetHelper.addPolicySetReference(policySet, policySetId);
            
            policyMgmtClient.updatePolicySet(policySet);
            
        } catch (NotFoundException e) {
            throw new CLIException("Critical error local PolicySet does not exists... probably a BUG", e);
        } 
        
    }
    
    protected void removePolicy(PolicyWizard policy) throws NotFoundException, RepositoryException, RemoteException {
        PolicySetType policySet;
        String policySetId;

        if (policy.isBlacklistPolicy())
            policySetId = BlacklistPolicySet.POLICY_SET_ID;
        else
            policySetId = ServiceClassPolicySet.POLICY_SET_ID;

            policySet = policyMgmtClient.getPolicySet(policySetId);
            
    }

    protected abstract boolean executeCommand(CommandLine commandLine) throws CLIException,
            ParseException, RemoteException;

    @Override
    protected boolean executeCommandService(CommandLine commandLine, ServiceClient serviceClient)
            throws CLIException, ParseException, RemoteException {

        policyMgmtClient = serviceClient.getPolicyManagementService(serviceClient.getTargetEndpoint()
                + SERVICE_NAME);

        return executeCommand(commandLine);
    }

    protected void initOpenSAML() {
        try {
            DefaultBootstrap.bootstrap();
            XMLConfigurator xmlConfigurator = new XMLConfigurator();

            // Needed because of a "bug" in opensaml 2.1.0... can be removed
            // when opensaml is updated
            xmlConfigurator.load(Configuration.class.getResourceAsStream("/opensaml_bugfix.xml"));
        } catch (ConfigurationException e) {
            throw new PAPConfigurationException("Error initializing OpenSAML library", e);
        }
    }

}
