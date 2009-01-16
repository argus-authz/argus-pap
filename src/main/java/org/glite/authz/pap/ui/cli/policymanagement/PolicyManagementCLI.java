package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.common.exceptions.PAPConfigurationException;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;

public abstract class PolicyManagementCLI extends ServiceCLI {

    protected static final String OPT_BLACKLIST = "bl";
    protected static final String LOPT_BLACKLIST = "blacklist";
    protected static final String OPT_BLACKLIST_DESCRIPTION = "List only \"blacklist\" policies.";
    protected static final String OPT_SERVICECLASS = "sc";
    protected static final String LOPT_SERVICECLASS = "serviceclass";
	protected static final String LOPT_SHOW_XACML = "show-xacml";
	protected static final String OPT_SHOW_XACML_DESCRIPTION = "XACML output.";
	protected static final String LOPT_PLAIN_FORMAT = "plain-format";
	protected static final String OPT_PLAIN_FORMAT_DESCRIPTION = "Do not group policies by \"resource_uri\" and \"service_class\".";
	
	protected static final String GENERIC_XACML_ERROR_MESSAGE = "Generic XACML policy, to see this policy specify the option --"
        + LOPT_SHOW_XACML + ".";

	protected static final String XACML_POLICY_MANAGEMENT_SERVICE_NAME = "XACMLPolicyManagementService";
	protected static final String HIGHLEVEL_POLICY_MANAGEMENT_SERVICE_NAME = "HighLevelPolicyManagementService";
	protected XACMLPolicyManagement xacmlPolicyMgmtClient;
	protected HighLevelPolicyManagement highlevelPolicyMgmtClient;

	public PolicyManagementCLI(String[] commandNameValues, String usage,
			String description, String longDescription) {
		super(commandNameValues, usage, description, longDescription);
	}

	protected abstract int executeCommand(CommandLine commandLine)
			throws CLIException, ParseException, RemoteException;

	@Override
	protected int executeCommandService(CommandLine commandLine,
			ServiceClient serviceClient) throws CLIException, ParseException,
			RemoteException {

		xacmlPolicyMgmtClient = serviceClient.getXACMLPolicyManagementService(serviceClient.getTargetEndpoint() + XACML_POLICY_MANAGEMENT_SERVICE_NAME);
		highlevelPolicyMgmtClient = serviceClient.getHighLevelPolicyManagementService(serviceClient.getTargetEndpoint() + HIGHLEVEL_POLICY_MANAGEMENT_SERVICE_NAME);

		return executeCommand(commandLine);
	}

	protected void initOpenSAML() {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			throw new PAPConfigurationException(
					"Error initializing OpenSAML library", e);
		}
	}

}
