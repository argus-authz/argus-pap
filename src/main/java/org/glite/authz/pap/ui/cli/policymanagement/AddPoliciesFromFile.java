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

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.common.xacml.wizard.XACMLWizard;
import org.glite.authz.pap.encoder.EncodingException;
import org.glite.authz.pap.encoder.PolicyFileEncoder;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddPoliciesFromFile extends PolicyManagementCLI {

    private static final Logger log = LoggerFactory.getLogger(AddPoliciesFromFile.class);

    private static final String[] commandNameValues = { "add-policies-from-file", "apf" };
    private static final String DESCRIPTION = "Add policies defined in the given file.\nParameters:\n"
	    + "<file>       policy file defining a set of resource elements or a set of action elements.\n"
	    + "[resourceId] resource id where insert actions into.\n";

    private static final String LONG_DESCRIPTION = "If <file> defines a set of resource elements \"resourceId\" must not "
	    + "be provided, otherwise if only action elements are defined \"resourceId\" indentifies the resource element "
	    + "in which inserting the given action elements into. By default elements are inserted at the bottom."
	    + " To change this behaviour two options can be used: \"--" + OPT_BEFORE_ID_LONG
	    + " <id>\" to insert before the given <id>, and \"--" + OPT_AFTER_ID_LONG
	    + " <id>\" to insert after the given <id>.";
    private static final String USAGE = "[options] <file> [resourceId]";
    private PolicyFileEncoder policyFileEncoder = new PolicyFileEncoder();
    private List<XACMLWizard> xacmlWizardList;
    private String resourceId = null;
    private String pivotId = null;
    private String alias = null;
    private boolean moveAfter = false;

    public AddPoliciesFromFile() {
	super(commandNameValues, USAGE, DESCRIPTION, LONG_DESCRIPTION);
    }

    private boolean addResources() throws RemoteException {

	for (XACMLWizard xacmlWizard : xacmlWizardList) {
	    if (!(xacmlWizard instanceof PolicySetWizard)) {
		System.out.println(String.format(
			"Error: found action element (%s). Only highlevel \"resource\" elements are allowed.",
			((PolicyWizard) xacmlWizard).getTagAndValue()));
		return false;
	    }
	}

	if (resourceId != null) {
	    System.out.println("Error cannot use \"resourceId\" to insert resource elements.");
	    return false;
	}

	int position = -1;

	if (pivotId != null) {
	    PolicySetType rootPolicySet = xacmlPolicyMgmtClient.getRootPolicySet(null);
	    position = PolicySetHelper.getPolicySetIdReferenceIndex(rootPolicySet, pivotId);
	    if (position == -1) {
		System.out.println("id \"" + pivotId + "\" not found (or it's not a resource id).");
		return false;
	    }
	    if (moveAfter) {
		position++;
	    }
	}

	boolean result = true;

	for (XACMLWizard xacmlWizard : xacmlWizardList) {

	    PolicySetWizard policySetWizard = (PolicySetWizard) xacmlWizard;

	    PolicySetType policySet = policySetWizard.getXACML();

	    policySet.getPolicyIdReferences().clear();

	    policySetWizard.releaseDOM();

	    String policySetId = xacmlPolicyMgmtClient.addPolicySet(alias, position, policySet);

	    if (position != -1) {
		position++;
	    }

	    if (policySetId == null) {
		System.out.println(String.format(
			"Error policy set not added: %s (id=%s). Skipping all the policies defined inside.",
			policySetWizard.getTagAndValue(), policySetWizard.getPolicySetId()));
		result = false;
		continue;
	    }

	    if (verboseMode) {
		System.out.println(
			String.format("Added policy set: %s (id=%s)", policySetWizard.getTagAndValue(), policySetId));
	    }

	    int size = policySetWizard.getPolicyWizardList().size();
	    PolicyType[] policyArray = new PolicyType[size];
	    String[] idPrefixArray = new String[size];
	    String[] tagAndValueArray = new String[size];

	    for (int i = 0; i < size; i++) {
		PolicyWizard policyWizard = policySetWizard.getPolicyWizardList().get(i);
		policyArray[i] = policyWizard.getXACML();
		TypeStringUtils.releaseUnneededMemory(policyArray[i]);
		idPrefixArray[i] = policyWizard.getPolicyIdPrefix();
		tagAndValueArray[i] = policyWizard.getTagAndValue();
		TypeStringUtils.releaseUnneededMemory(policyWizard);
	    }

	    String[] policyIdArray = xacmlPolicyMgmtClient.addPolicies(alias, 0, policySetId, idPrefixArray,
		    policyArray);

	    for (int i = 0; i < size; i++) {
		String policyId = policyIdArray[i];
		String tagAndValue = tagAndValueArray[i];

		if (policyId == null) {
		    System.out.println(String.format("Error policy not added: %s", tagAndValue));
		    result = false;
		    continue;
		}

		if (verboseMode) {
		    System.out.println(String.format("Added policy: %s (id=%s)", tagAndValue, policyId));
		}
	    }

	    if (verboseMode) {
		System.out.println();
	    }
	}
	return result;
    }

    private boolean addActions() throws RemoteException {

	List<PolicyWizard> policyWizardList = new ArrayList<PolicyWizard>(xacmlWizardList.size());

	for (XACMLWizard xacmlWizard : xacmlWizardList) {
	    if (!(xacmlWizard instanceof PolicyWizard)) {
		System.out.println(String.format(
			"Error: found resource element (%s). Only \"action\" highlevel elements are allowed.",
			((PolicySetWizard) xacmlWizard).getTagAndValue()));
		return false;
	    }
	    policyWizardList.add((PolicyWizard) xacmlWizard);
	}

	if (resourceId == null) {
	    System.out.println("Error \"resourceId\" is needed to insert action elements");
	    return false;
	}

	PolicySetType targetPolicySet = xacmlPolicyMgmtClient.getPolicySet(alias, resourceId);

	int position = -1;

	if (pivotId != null) {
	    position = PolicySetHelper.getPolicyIdReferenceIndex(targetPolicySet, pivotId);
	    TypeStringUtils.releaseUnneededMemory(targetPolicySet);
	    if (position == -1) {
		System.out.println("id \"" + pivotId + "\" not found inside resource the given resource (id = \""
			+ resourceId + "\").");
		return false;
	    }
	    if (moveAfter) {
		position++;
	    }
	}

	boolean result = true;

	int size = xacmlWizardList.size();
	PolicyType[] policyArray = new PolicyType[size];
	String[] idPrefixArray = new String[size];
	String[] tagAndValueArray = new String[size];

	for (int i = 0; i < size; i++) {
	    PolicyWizard policyWizard = policyWizardList.get(i);
	    policyArray[i] = policyWizard.getXACML();
	    TypeStringUtils.releaseUnneededMemory(policyArray[i]);
	    idPrefixArray[i] = policyWizard.getPolicyIdPrefix();
	    tagAndValueArray[i] = policyWizard.getTagAndValue();
	    TypeStringUtils.releaseUnneededMemory(policyWizard);
	    policyWizard = null;
	}

	log.debug("Inserting actions into position: " + position);

	String[] policyIdArray = xacmlPolicyMgmtClient.addPolicies(alias, position, resourceId, idPrefixArray,
		policyArray);
	for (int i = 0; i < size; i++) {
	    String policyId = policyIdArray[i];
	    String tagAndValue = tagAndValueArray[i];

	    if (policyId == null) {
		System.out.println(String.format("Error policy not added: %s", tagAndValue));
		result = false;
		continue;
	    }

	    if (verboseMode) {
		System.out.println(String.format("Added policy: %s (id=%s)", tagAndValue, policyId));
	    }
	}

	if (verboseMode) {
	    System.out.println();
	}
	return result;
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
	Options options = new Options();
	options.addOption(OptionBuilder.hasArg(true).withDescription(OPT_AFTER_ID_DESCRIPTION)
		.withLongOpt(OPT_AFTER_ID_LONG).withArgName("id").create());
	options.addOption(OptionBuilder.hasArg(true).withDescription(OPT_BEFORE_ID_DESCRIPTION)
		.withLongOpt(OPT_BEFORE_ID_LONG).withArgName("id").create());
	options.addOption(OptionBuilder.hasArg(true).withDescription(OPT_PAPALIAS_DESCRIPTION)
		.withLongOpt(OPT_PAPALIAS_LONG).withArgName("alias").create());
	return options;
    }

    @Override
    protected int executeCommand(final CommandLine commandLine) throws CLIException, ParseException, RemoteException {
	String[] args = commandLine.getArgs();

	if ((args.length < 2) || (args.length > 3)) {
	    throw new ParseException("Wrong number of arguments.");
	}

	if (commandLine.hasOption(OPT_PAPALIAS_LONG)) {
	    alias = commandLine.getOptionValue(OPT_PAPALIAS_LONG);
	}

	if ((args.length == 3)) {
	    resourceId = args[2];
	}

	if (commandLine.hasOption(OPT_BEFORE_ID_LONG)) {
	    pivotId = commandLine.getOptionValue(OPT_BEFORE_ID_LONG);
	    moveAfter = false;
	}

	if (commandLine.hasOption(OPT_AFTER_ID_LONG)) {
	    pivotId = commandLine.getOptionValue(OPT_AFTER_ID_LONG);
	    moveAfter = true;
	}

	log.trace("args.lengh=" + args.length);
	log.trace("resourceId=" + resourceId);
	log.trace("pivotId=" + pivotId);
	log.trace("moveAfter=" + moveAfter);

	File file = new File(args[1]);

	XACMLPolicyCLIUtils.initOpenSAMLAndAttributeWizard();

	try {
	    xacmlWizardList = policyFileEncoder.parse(file);
	} catch (EncodingException e) {
	    System.out.println("Syntax error no policies has been added from file:" + file.getAbsolutePath());
	    System.out.println("Reason:");
	    System.out.println(e.getMessage());
	    return ExitStatus.FAILURE.ordinal();
	}

	if (xacmlWizardList.isEmpty()) {
	    System.out.println("No policies defined in the given file");
	    return ExitStatus.FAILURE.ordinal();
	}

	boolean result;

	if (xacmlWizardList.get(0) instanceof PolicySetWizard) {
	    result = addResources();
	} else {
	    result = addActions();
	}

	if (result == true) {
	    if (verboseMode) {
		System.out.println("Success: policies has been added from file " + file.getAbsolutePath());
	    }
	} else {
	    return ExitStatus.FAILURE.ordinal();
	}
	return ExitStatus.SUCCESS.ordinal();
    }
}
