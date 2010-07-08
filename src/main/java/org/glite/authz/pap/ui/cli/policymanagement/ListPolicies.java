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

package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListPolicies extends PolicyManagementCLI {

	private static final String[] commandNameValues = { "list-policies", "lp" };
	private static final String DESCRIPTION = "List policies. By default the policies of the default pap are listed unless option --"
			+ OPT_PAPALIAS_LONG + " is specified.";
	private static final Logger log = LoggerFactory
			.getLogger(ListPolicies.class);
	private static final String USAGE = "[options]";

	private static final String OPT_ACTION_LONG = "action";
	private static final String OPT_ACTION_LONG_DESCRIPTION = "filter by action <arg>";

	private static final String OPT_RESOURCE_LONG = "resource";
	private static final String OPT_RESOURCE_LONG_DESCRIPTION = "filter by resource <arg>";

	public ListPolicies() {
		super(commandNameValues, USAGE, DESCRIPTION, null);
	}

	
	private String actionFilter = null;
	private String resourceFilter = null;
	
	protected boolean listPolicies(String papAlias, boolean showIds,
			boolean showRuleId, boolean xacmlOutput) throws RemoteException {

		boolean foundMatchingPolicies = false;

		PolicySetType[] policySetArray;

		policySetArray = xacmlPolicyMgmtClient.listPolicySets(papAlias);

		if (policySetArray.length == 0) {
			throw new CLIException(
					"Error: the repository seems to be corrupted, no policy sets have been found");
		}

		PolicyType[] policyArray;

		policyArray = xacmlPolicyMgmtClient.listPolicies(papAlias);

		List<PolicyWizard> policyWizardList = new ArrayList<PolicyWizard>(policyArray.length);

		for (PolicyType policy : policyArray) {
			
			// Filtering by action
			// TODO: how to handle wildcards? Should we use regexp match?
			String actionValue = PolicyWizard.getActionValue(policy);
			if (actionFilter != null && !actionValue.equals(actionFilter))
				continue;
			
			PolicyWizard policyWizard = new PolicyWizard(policy);
			policyWizardList.add(policyWizard);
			policyWizard.releaseChildrenDOM();
			policyWizard.releaseDOM();
		}

		policyArray = null;

		PolicySetType localRootPolicySet = policySetArray[0];

		for (String policySetId : PolicySetHelper
				.getPolicySetIdReferencesValues(localRootPolicySet)) {

			PolicySetType policySet = null;

			for (PolicySetType policySetElem : policySetArray) {
				if (policySetId.equals(policySetElem.getPolicySetId())) {
					policySet = policySetElem;
					break;
				}
			}

			if (policySet == null) {
				throw new CLIException(
						"Error: the repository seems to be corrupted, policy set not found: "
								+ policySetId);
			}

			try {
				
				String targetResourceValue = PolicySetWizard.getResourceValue(policySet);
				
				// Filter by resource at the policy set level
				// TODO: how to handle wildcards? Should we use regexp match?
				if (resourceFilter!=null && !targetResourceValue.equals(resourceFilter)){
					continue;
				}
				
				
				// If filtering by action ruled out policies for this resource proceed to the
				// next policy set
				if (policyWizardList.isEmpty()){
					continue;
				}
				PolicySetWizard policySetWizard = new PolicySetWizard(
						policySet, policyWizardList, null);
				
				System.out.println();

				if (xacmlOutput) {
					
					System.out.println(policySetWizard.toXACMLString());
				
				} else {
					
					System.out.println(policySetWizard.toFormattedString(
							showIds, showRuleId));
				}

			} catch (UnsupportedPolicySetWizardException e) {
				log.error("Unsupported Policy/PolicySet", e);
				System.out.println("id=" + policySetId + ": "
						+ GENERIC_XACML_ERROR_MESSAGE);
			}

			foundMatchingPolicies = true;
		}

		return foundMatchingPolicies;
	}

	@SuppressWarnings("static-access")
	@Override
	protected Options defineCommandOptions() {
		Options options = new Options();
		options.addOption(OptionBuilder.hasArg(false).withDescription(
				OPT_SHOW_XACML_DESCRIPTION).withLongOpt(OPT_SHOW_XACML_LONG)
				.create());
		options.addOption(OptionBuilder.hasArg(false).withDescription(
				OPT_SHOW_RA_IDS_DESCRIPTION).withLongOpt(OPT_SHOW_IDS_LONG)
				.create(OPT_SHOW_RA_IDS));
		options.addOption(OptionBuilder.hasArg(false).withDescription(
				OPT_SHOW_ALL_IDS_DESCRIPTION)
				.withLongOpt(OPT_SHOW_ALL_IDS_LONG).create(OPT_SHOW_ALL_IDS));
		options.addOption(OptionBuilder.hasArg(false).withDescription(
				OPT_ALLPAPS_DESCRIPTION).withLongOpt(OPT_ALL_LONG).create());
		options.addOption(OptionBuilder.hasArgs().withDescription(
				OPT_PAPALIAS_DESCRIPTION).withLongOpt(OPT_PAPALIAS_LONG)
				.create());

		options.addOption(OptionBuilder.hasArg(true).withDescription(
				OPT_ACTION_LONG_DESCRIPTION).withLongOpt(OPT_ACTION_LONG)
				.create());

		options.addOption(OptionBuilder.hasArg(true).withDescription(
				OPT_RESOURCE_LONG_DESCRIPTION).withLongOpt(OPT_RESOURCE_LONG)
				.create());
		return options;
	}

	@Override
	protected int executeCommand(CommandLine commandLine)
			throws ParseException, RemoteException {
		boolean xacmlOutput = false;
		boolean showIds = false;
		boolean showRulesId = false;

		if (commandLine.hasOption(OPT_SHOW_XACML_LONG)) {
			xacmlOutput = true;
		}

		if (commandLine.hasOption(OPT_SHOW_RA_IDS)) {
			showIds = true;
		}

		if (commandLine.hasOption(OPT_SHOW_ALL_IDS_LONG)) {
			showRulesId = true;
			showIds = true;
		}

		if (commandLine.hasOption(OPT_ACTION_LONG)){
			actionFilter = commandLine.getOptionValue(OPT_ACTION_LONG);
		}
		
		if (commandLine.hasOption(OPT_RESOURCE_LONG)){
			resourceFilter = commandLine.getOptionValue(OPT_RESOURCE_LONG);
		}
		
		String[] papAliasArray = null;
		String[] papInfoArray = null;

		if (commandLine.hasOption(OPT_ALL_LONG)) {

			Pap[] papArray = papMgmtClient.getAllPaps();
			papAliasArray = new String[papArray.length];
			for (int i = 0; i < papArray.length; i++) {
				papAliasArray[i] = papArray[i].getAlias();
			}
			papInfoArray = getPAPInfoArray(papAliasArray, papArray);

		} else if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {

			papAliasArray = commandLine.getOptionValues(OPT_PAPALIAS_LONG);
			papInfoArray = getPAPInfoArray(papAliasArray, null);

		} else {

			Pap pap = Pap.makeDefaultPAP();

			Pap[] papArray = new Pap[1];
			papArray[0] = pap;

			papAliasArray = new String[1];
			papAliasArray[0] = pap.getAlias();

			papInfoArray = getPAPInfoArray(papAliasArray, papArray);
		}

		XACMLPolicyCLIUtils.initOpenSAMLAndAttributeWizard();

		for (int i = 0; i < papAliasArray.length; i++) {

			System.out.println();
			System.out.println(papInfoArray[i]);

			boolean policiesFound = listPolicies(papAliasArray[i], showIds,
					showRulesId, xacmlOutput);

			if (!policiesFound) {
				printOutputMessage("No policies has been found.");
			}
		}

		return ExitStatus.SUCCESS.ordinal();
	}

	private String[] getPAPInfoArray(String[] papAliasArray, Pap[] papArray)
			throws RemoteException {

		int size = papAliasArray.length;
		String[] papInfoArray = new String[size];

		for (int i = 0; i < size; i++) {

			String alias = papAliasArray[i];

			Pap pap;

			if (papArray != null) {
				pap = papArray[i];
			} else {
				pap = papMgmtClient.getPap(alias);
			}

			if (pap.isLocal()) {
				papInfoArray[i] = String.format("%s (local):", pap.getAlias());
			} else {
				papInfoArray[i] = String.format("%s (%s:%s):", pap.getAlias(),
						pap.getHostname(), pap.getPort());
			}
		}
		return papInfoArray;
	}
}
