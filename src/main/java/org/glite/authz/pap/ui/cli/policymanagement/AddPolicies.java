package org.glite.authz.pap.ui.cli.policymanagement;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.XACMLWizard;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;

public class AddPolicies extends PolicyManagementCLI {

	private static final String[] commandNameValues = { "add-policies-from-file", "ap" };
	private static final String DESCRIPTION = "Add policies defined in the given files.";
	private static final String USAGE = "<file> [[file] ...] [options]";
	private PolicyFileEncoder policyFileEncoder = new PolicyFileEncoder();

	public AddPolicies() {
		super(commandNameValues, USAGE, DESCRIPTION, null);
	}

	private boolean addPolicies(String fileName) throws EncodingException, RemoteException {

		boolean result = true;

		File file = new File(fileName);

		XACMLPolicyCLIUtils.initOpenSAML();

		List<XACMLWizard> policyList = policyFileEncoder.parse(file);

		for (XACMLWizard xacmlWizard : policyList) {
		    
		    if (!(xacmlWizard instanceof PolicySetWizard)) {
		        System.out.println("Error: \"resource\" element not defined");
		        result = false;
		        continue;
		    }
		    
		    PolicySetWizard policySetWizard = (PolicySetWizard) xacmlWizard;

			PolicySetType policySet = policySetWizard.getXACML();
			
			policySet.getPolicyIdReferences().clear();
			
			String policySetId = xacmlPolicyMgmtClient.addPolicySet(-1, policySet);

			if (policySetId == null) {
				System.out.println(String.format(
					"Error policy set not added: %s (id=%s). Skipping all the policies defined inside.",
					policySetWizard.getTagAndValue(), policySetWizard.getPolicySetId()));
				result = false;
				continue;
			}

			if (verboseMode) {
				System.out.println(String.format("Added policy set: %s (id=%s)", policySetWizard
						.getTagAndValue(), policySetId));
			}

			for (PolicyWizard policyWizard : policySetWizard.getPolicyWizardList()) {
				String policyId = xacmlPolicyMgmtClient.addPolicy(-1, policySetId,
					policyWizard.getPolicyIdPrefix(), policyWizard.getXACML());

				if (policyId == null) {
					System.out.println(String.format("Error policy not added: %s (id=%s)", policyWizard
							.getTagAndValue(), policyWizard.getPolicyId()));
					result = false;
					continue;
				}

				if (verboseMode) {
					System.out.println(String.format("Added policy: %s (id=%s)", policyWizard
							.getTagAndValue(), policyWizard.getPolicyId()));
				}
			}

			if (verboseMode) {
				System.out.println();
			}
		}
		return result;
	}

	@Override
	protected Options defineCommandOptions() {
		return null;
	}

	@Override
	protected int executeCommand(CommandLine commandLine) throws CLIException, ParseException,
			RemoteException {
		String[] args = commandLine.getArgs();

		if (args.length < 2)
			throw new ParseException("No input files defined.");

		for (int i = 1; i < args.length; i++) {
			File file = new File(args[i]);
			if (!file.exists())
				throw new ParseException("File not found: " + file.getAbsolutePath());
		}

		boolean partialSuccess = false;
		boolean failure = false;

		for (int i = 1; i < args.length; i++) {

			String fileName = args[i];

			try {

				boolean result = addPolicies(fileName);

				if (result == true) {
					partialSuccess = true;

					if (verboseMode) {
						System.out.println("Success: policies has been added from file " + fileName);
					}
				} else {
					System.out.println("Error addind policies from file " + fileName);
					failure = true;
				}

			} catch (EncodingException e) {
				failure = true;
				System.out.println("Syntax error. Skipping file (no policies has been added):" + fileName);
				System.out.println(e.getMessage());
				continue;
			}
		}

		if (failure && !partialSuccess)
			return ExitStatus.FAILURE.ordinal();

		if (failure && partialSuccess)
			return ExitStatus.PARTIAL_SUCCESS.ordinal();

		return ExitStatus.SUCCESS.ordinal();

	}
}
