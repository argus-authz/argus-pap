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

    protected static String OPT_PAPALIAS_LONG = "pap";
    protected static String OPT_PAPALIAS_DESCRIPTION = "Select <arg> as pap";
    protected static String OPT_ALL_LONG = "all";
    protected static String OPT_ALLPAPS_DESCRIPTION = "Select all paps";
    
    protected static String OPT_AFTER_ID_LONG = "after";
    protected static String OPT_AFTER_ID_DESCRIPTION = "place element after the given <id>";
    protected static String OPT_BEFORE_ID_LONG = "before";
    protected static String OPT_BEFORE_ID_DESCRIPTION = "place element before the given <id>";
    
    protected static String OPT_ACTION = "a";
    protected static String OPT_ACTION_DESCRIPTION = "Specify an action value.";
    protected static String OPT_ACTION_LONG = "action";
    protected static String OPT_RESOURCE = "r";
    protected static String OPT_RESOURCE_DESCRIPTION = "Specify a resource value.";
    protected static String OPT_RESOURCE_LONG = "resource";
    protected static final String OPT_POLICY_DESCRIPTION = "d";
    protected static final String OPT_POLICY_DESCRIPTION_DESCRIPTION = "Description";
    protected static final String OPT_POLICY_DESCRIPTION_LONG = "description";
    protected static final String OPT_SHOW_RA_IDS = "srai";
    protected static final String OPT_SHOW_RA_IDS_DESCRIPTION = "Show resource and action ids";
    protected static final String OPT_SHOW_IDS_LONG = "show-ra-ids";
    protected static final String OPT_SHOW_ALL_IDS_DESCRIPTION = "Show all ids (resource, action and rule ids)";
    protected static final String OPT_SHOW_ALL_IDS_LONG = "show-all-ids";
    protected static final String OPT_SHOW_ALL_IDS = "sai";
    protected static final String OPT_SHOW_XACML_DESCRIPTION = "use pure XACML (do not convert policies into the simplified policy notation)";
    protected static final String OPT_SHOW_XACML_LONG = "show-xacml";
    protected static final String GENERIC_XACML_ERROR_MESSAGE = "Generic XACML policy, to see this policy specify the option --"
            + OPT_SHOW_XACML_LONG + ".";

    protected HighLevelPolicyManagement highlevelPolicyMgmtClient;
    protected PAPManagement papMgmtClient;
    protected XACMLPolicyManagement xacmlPolicyMgmtClient;

    public PolicyManagementCLI(String[] commandNameValues, String usage, String description, String longDescription) {
        super(commandNameValues, usage, description, longDescription);
    }

    protected abstract int executeCommand(CommandLine commandLine) throws CLIException, ParseException, RemoteException;

    @Override
    protected int executeCommandService(CommandLine commandLine, ServiceClient serviceClient) throws CLIException,
            ParseException, RemoteException {

        xacmlPolicyMgmtClient = serviceClient.getXACMLPolicyManagementService(serviceClient.getTargetEndpoint()
                + serviceClient.getXACMLPolicyManagementServiceName());
        highlevelPolicyMgmtClient = serviceClient.getHighLevelPolicyManagementService(serviceClient.getTargetEndpoint()
                + serviceClient.getHighLevelPolicyManagementServiceName());
        papMgmtClient = serviceClient.getPAPManagementService(serviceClient.getTargetEndpoint()
                + serviceClient.getPAPManagementServiceName());

        return executeCommand(commandLine);
    }

}
