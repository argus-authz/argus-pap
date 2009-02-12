package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.client.ServiceClient;
import org.glite.authz.pap.services.highlevel_policy_management.axis_skeletons.HighLevelPolicyManagement;
import org.glite.authz.pap.services.pap_management.axis_skeletons.PAPManagement;
import org.glite.authz.pap.services.xacml_policy_management.axis_skeletons.XACMLPolicyManagement;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.cli.ServiceCLI;

public abstract class PolicyManagementCLI extends ServiceCLI {

    protected static final String OPT_BLACKLIST = "bl";
    protected static final String OPT_BLACKLIST_LONG = "blacklist";
    protected static final String OPT_BLACKLIST_DESCRIPTION = "List only \"blacklist\" policies.";
    protected static final String OPT_SERVICECLASS = "sc";
    protected static final String OPT_SERVICECLASS_LONG = "serviceclass";
	protected static final String OPT_SHOW_XACML_LONG = "show-xacml";
	protected static final String OPT_SHOW_XACML_DESCRIPTION = "XACML output.";
	protected static final String OPT_PLAIN_FORMAT_LONG = "plain-format";
	protected static final String OPT_PLAIN_FORMAT_DESCRIPTION = "Do not group policies by \"resource_uri\" and \"service_class\".";
	protected static final String OPT_LIST_ONE_BY_ONE = "obo";
	protected static final String OPT_LIST_ONE_BY_ONE_LONG = "one-by-one";
	protected static final String OPT_LIST_ONE_BY_ONE_DESCRIPTION = "Ask the server one policy at a time instead of all in once";
	protected static final String OPT_POLICY_DESCRIPTION = "d";
	protected static final String OPT_POLICY_DESCRIPTION_LONG = "description";
	protected static final String OPT_POLICY_DESCRIPTION_DESCRIPTION = "Description";
	
	protected static final String GENERIC_XACML_ERROR_MESSAGE = "Generic XACML policy, to see this policy specify the option --"
        + OPT_SHOW_XACML_LONG + ".";

	protected static final String XACML_POLICY_MANAGEMENT_SERVICE_NAME = "XACMLPolicyManagementService";
	protected static final String HIGHLEVEL_POLICY_MANAGEMENT_SERVICE_NAME = "HighLevelPolicyManagementService";
	
	protected XACMLPolicyManagement xacmlPolicyMgmtClient;
	protected HighLevelPolicyManagement highlevelPolicyMgmtClient;
	protected PAPManagement papMgmtClient;

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

}
