package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.utils.xacml.XMLObjectHelper;
import org.glite.authz.pap.ui.cli.CLIException;
import org.glite.authz.pap.ui.wizard.LocalPolicySetWizard;
import org.glite.authz.pap.ui.wizard.PolicyWizard;
import org.glite.authz.pap.ui.wizard.UnsupportedPolicyException;
import org.opensaml.xacml.policy.PolicyType;

public class ListPAPPolicies extends PolicyManagementCLI {

	private static final String LOPT_SHOW_XACML = "show-xacml";
	private static final String GENERIC_XACML_ERROR_MESSAGE = "Generic XACML policy, to see this policy specify the option --"
			+ LOPT_SHOW_XACML;

	private static final String USAGE = "<papId> [options]";
	private static final String[] commandNameValues = { "list-pap-policies",
			"lpp" };
	private static final String DESCRIPTION = "List cached policies of the remote PAP \"papId\". ";

	public ListPAPPolicies() {
		super(commandNameValues, USAGE, DESCRIPTION, null);
	}

	@Override
	protected void executeCommand(CommandLine commandLine) throws CLIException,
			ParseException, RemoteException {

		String[] args = commandLine.getArgs();

		if (args.length != 2)
			throw new ParseException("Missing argument <papId>");

		boolean xacmlOutput = false;
		boolean plainFormat = false;
		
		if (commandLine.hasOption(LOPT_SHOW_XACML))
			xacmlOutput = true;
		
		if (commandLine.hasOption(LOPT_PLAIN_FORMAT))
			plainFormat = true;

		initOpenSAML();

		List<PolicyType> policyList = policyMgmtClient.listPolicies(args[1]);
		
		if (xacmlOutput || plainFormat)
			listUsingPlaingFormat(policyList, xacmlOutput);
		else
			listUsingGroupedFormat(policyList);

	}

	private void listUsingGroupedFormat(List<PolicyType> policyList) {
		
		for (PolicyType policy:policyList) {
			
			LocalPolicySetWizard localPolicySetWizard = new LocalPolicySetWizard();
			
			try {
				PolicyWizard policyWizard = new PolicyWizard(policy);
				localPolicySetWizard.addPolicy(policyWizard);
			} catch (UnsupportedPolicyException e) {
				System.out.println("id=" + policy.getPolicyId() + ": " + GENERIC_XACML_ERROR_MESSAGE);
			}
			
			localPolicySetWizard.printFormattedBlacklistPolicies(System.out);
			
			localPolicySetWizard.printFormattedServiceClassPolicies(System.out);
			
		}
	}

	private void listUsingPlaingFormat(List<PolicyType> policyList,
			boolean xacmlOutput) {

		for (PolicyType policy : policyList) {

			String policyString;

			try {
				PolicyWizard policyWizard = new PolicyWizard(policy);

				if (xacmlOutput)
					policyString = XMLObjectHelper.toString(policy);
				else
					policyString = policyWizard.toFormattedString();

			} catch (UnsupportedPolicyException e) {
				policyString = GENERIC_XACML_ERROR_MESSAGE;
			}

			System.out.println(policyString);

		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected Options defineCommandOptions() {
		Options options = new Options();

		options.addOption(OptionBuilder.hasArg(false).withDescription(
				OPT_SHOW_XACML_DESCRIPTION).withLongOpt(LOPT_SHOW_XACML)
				.create());

		options.addOption(OptionBuilder.hasArg(false).withDescription(
				OPT_PLAIN_FORMAT_DESCRIPTION).withLongOpt(LOPT_PLAIN_FORMAT)
				.create());

		return options;
	}

}
