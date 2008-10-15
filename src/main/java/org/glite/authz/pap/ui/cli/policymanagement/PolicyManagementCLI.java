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

	protected static final String LOPT_SHOW_XACML = "show-xacml";
	protected static final String OPT_SHOW_XACML_DESCRIPTION = "XACML output.";
	protected static final String LOPT_PLAIN_FORMAT = "plain-format";
	protected static final String OPT_PLAIN_FORMAT_DESCRIPTION = "Do not group policies by \"resource_uri\" and \"service_class\".";
	
	protected static final String GENERIC_XACML_ERROR_MESSAGE = "Generic XACML policy, to see this policy specify the option --"
        + LOPT_SHOW_XACML + ".";

	protected static final String SERVICE_NAME = "PolicyManagementService";
	protected PolicyManagementService policyMgmtClient;

	public PolicyManagementCLI(String[] commandNameValues, String usage,
			String description, String longDescription) {
		super(commandNameValues, usage, description, longDescription);
	}

	protected void addPolicy(PolicyWizard policy) throws CLIException,
			RemoteException {

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

		String policyId = policyMgmtClient.storePolicy(policy
				.getPolicyIdPrefix(), policy.getPolicyType());

		PolicySetHelper.addPolicyReference(policySet, policyId);

		if (updateOperation)
			policyMgmtClient.updatePolicySet(policySet);
		else {
			policyMgmtClient.storePolicySet(null, policySet);

			PolicySetType localPolicySet = policyMgmtClient
					.getPolicySet(PAP.localPAPId);

			PolicySetHelper.addPolicySetReference(localPolicySet, policySetId);

			policyMgmtClient.updatePolicySet(localPolicySet);
		}

	}

	protected abstract void executeCommand(CommandLine commandLine)
			throws CLIException, ParseException, RemoteException;

	@Override
	protected void executeCommandService(CommandLine commandLine,
			ServiceClient serviceClient) throws CLIException, ParseException,
			RemoteException {

		policyMgmtClient = serviceClient
				.getPolicyManagementService(serviceClient.getTargetEndpoint()
						+ SERVICE_NAME);

		executeCommand(commandLine);
	}

	protected void initOpenSAML() {
		try {
			DefaultBootstrap.bootstrap();
			XMLConfigurator xmlConfigurator = new XMLConfigurator();

			// Needed because of a "bug" in opensaml 2.1.0... can be removed
			// when opensaml is updated
			xmlConfigurator.load(Configuration.class
					.getResourceAsStream("/opensaml_bugfix.xml"));
		} catch (ConfigurationException e) {
			throw new PAPConfigurationException(
					"Error initializing OpenSAML library", e);
		}
	}

	protected void removePolicy(PolicyWizard policy) throws NotFoundException,
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
