package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedPolicySetWizardException;
import org.glite.authz.pap.ui.cli.CLIException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;

public class ListPolicies extends PolicyManagementCLI {

    private static final String OPT_SHOW_IDS_LONG = "show-ids";
    private static final String OPT_SHOW_IDS_DESCRIPTION = "The output format is the same as the one used for the "
            + "simplified policy language. Policy ids are not written, therefore the output can be saved to a file and "
            + "given in input to the \"add-policies-from-file\" option.";
    private static final String USAGE = "[options]";
    private static final String[] commandNameValues = { "list-policies", "lp" };
    private static final String DESCRIPTION = "List policies authored by the PAP.";

    public ListPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();

        options.addOption(OptionBuilder.hasArg(false).withDescription("List only \"public\" policies.").withLongOpt(
                OPT_PUBLIC_LONG).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription("List only \"private\" policies.").withLongOpt(
                OPT_PRIVATE_LONG).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_XACML_DESCRIPTION)
                .withLongOpt(OPT_SHOW_XACML_LONG).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_IDS_DESCRIPTION).withLongOpt(OPT_SHOW_IDS_LONG)
                .create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_LIST_ONE_BY_ONE_DESCRIPTION).withLongOpt(
                OPT_LIST_ONE_BY_ONE_LONG).create(OPT_LIST_ONE_BY_ONE));

        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        boolean showPrivate = true;
        boolean showPublic = true;
        boolean xacmlOutput = false;
        boolean showIds = false;
        boolean getPoliciesOneByOne = false;

        if (commandLine.hasOption(OPT_PRIVATE_LONG))
            showPublic = false;

        if (commandLine.hasOption(OPT_PUBLIC_LONG))
            showPrivate = false;

        if (commandLine.hasOption(OPT_SHOW_XACML_LONG))
            xacmlOutput = true;

        if (commandLine.hasOption(OPT_LIST_ONE_BY_ONE))
            getPoliciesOneByOne = true;

        if (commandLine.hasOption(OPT_SHOW_IDS_LONG))
            showIds = true;

        XACMLPolicyCLIUtils.initOpenSAML();

        PAPPolicyIterator policyIter = new PAPPolicyIterator(xacmlPolicyMgmtClient, null, getPoliciesOneByOne);

        policyIter.init();

        if (policyIter.getNumberOfPolicies() == 0) {
            printOutputMessage("No policies has been found.");
            return ExitStatus.SUCCESS.ordinal();
        }

        boolean policiesFound;

        policiesFound = listUsingGroupedFormat(policyIter, showPrivate, showPublic, showIds);

        if (!policiesFound)
            printOutputMessage(noPoliciesFoundMessage(showPrivate, showPublic));

        return ExitStatus.SUCCESS.ordinal();
    }

    protected static boolean listUsingGroupedFormat(PAPPolicyIterator policyIter, boolean showPrivate, boolean showPublic,
            boolean showIds) {

        boolean somethingHasBeenSelected = false;

        PolicySetType[] policySetArray = policyIter.getAllPolicySets();

        if (policySetArray.length == 0) {
            throw new CLIException("Error: the repository seems to be corrupted, no policy sets have been found");
        }

        PolicyType[] policyArray = policyIter.getAllPolicies();

        PolicySetType localRootPolicySet = policySetArray[0];

        for (String policySetId : PolicySetHelper.getPolicySetIdReferencesValues(localRootPolicySet)) {

            PolicySetType policySet = null;

            for (PolicySetType policySetElem : policySetArray) {
                if (policySetId.equals(policySetElem.getPolicySetId())) {
                    policySet = policySetElem;
                    break;
                }
            }

            if (policySet == null) {
                throw new CLIException("Error: the repository seems to be corrupted, policy set not found: " + policySetId);
            }

            try {
                PolicySetWizard policySetWizard = new PolicySetWizard(policySet, policyArray, null);
                System.out.println();
                System.out.println(policySetWizard.toFormattedString(showIds));
            } catch (UnsupportedPolicySetWizardException e) {
                System.out.println("id=" + policySetId + ": " + GENERIC_XACML_ERROR_MESSAGE);
            }

            somethingHasBeenSelected = true;
        }

        return somethingHasBeenSelected;

        // LocalPolicySetWizard localPolicySetWizard = new
        // LocalPolicySetWizard();
        //
        // while (policyIter.hasNext()) {
        //
        // PolicyType policy = policyIter.next();
        //
        // try {
        // PolicyWizard policyWizard = new PolicyWizard(policy);
        //
        // if (!policyMustBeShown(policyWizard.isPrivate(), showPrivate,
        // showPublic))
        // continue;
        //
        // localPolicySetWizard.addPolicy(policyWizard);
        // somethingHasBeenSelected = true;
        //
        // } catch (UnsupportedPolicyException e) {
        // System.out.println("id=" + policy.getPolicyId() + ": " +
        // GENERIC_XACML_ERROR_MESSAGE);
        // }
        //
        // }
        //
        // localPolicySetWizard.printFormattedBlacklistPolicies(System.out,
        // noId);
        // localPolicySetWizard.printFormattedServiceClassPolicies(System.out,
        // noId);
        //
        // return somethingHasBeenSelected;
    }

    private static boolean policyMustBeShown(boolean isPrivate, boolean showPrivate, boolean showPublic) {

        boolean isPublic = !isPrivate;

        if (!((showPrivate && isPrivate) || (showPublic && isPublic)))
            return false;

        return true;
    }

    protected static String noPoliciesFoundMessage(boolean showPrivate, boolean showPublic) {

        String visibilityMsg;

        if (showPrivate && showPublic)
            visibilityMsg = "";
        else if (showPrivate)
            visibilityMsg = "\"private\"";
        else
            visibilityMsg = "\"public\"";

        String msg = "No " + visibilityMsg;

        if (visibilityMsg.length() != 0)
            msg += ", ";

        msg += "policies has been found.";

        return msg;
    }

}
