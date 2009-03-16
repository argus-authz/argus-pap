package org.glite.authz.pap.ui.cli.policymanagement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
    private static final String DESCRIPTION = "List policies authored by the PAP.";
    private static final Logger log = LoggerFactory.getLogger(ListPolicies.class);
    private static final String USAGE = "[options]";

    public ListPolicies() {
        super(commandNameValues, USAGE, DESCRIPTION, null);
    }

    protected static boolean listPolicies(PAPPolicyIterator policyIter, boolean showIds, boolean showRulseId, boolean xacmlOutput) {

        boolean somethingHasBeenSelected = false;

        PolicySetType[] policySetArray = policyIter.getAllPolicySets();
        

        if (policySetArray.length == 0) {
            throw new CLIException("Error: the repository seems to be corrupted, no policy sets have been found");
        }

        PolicyType[] policyArray = policyIter.getAllPolicies();
        
        List<PolicyWizard> policyWizardList = new ArrayList<PolicyWizard>(policyArray.length);
        
        for (PolicyType policy : policyArray) {
            PolicyWizard policyWizard = new PolicyWizard(policy); 
            policyWizardList.add(policyWizard);
            policyWizard.releaseChildrenDOM();
            policyWizard.releaseDOM();
        }
        
        policyArray = null;
        
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
                PolicySetWizard policySetWizard = new PolicySetWizard(policySet, policyWizardList, null);
                System.out.println();

                if (xacmlOutput) {
                    System.out.println(policySetWizard.toXACMLString());
                } else {
                    System.out.println(policySetWizard.toFormattedString(showIds, showRulseId));
                }
                
            } catch (UnsupportedPolicySetWizardException e) {
                log.error("Unsupported Policy/PolicySet", e);
                System.out.println("id=" + policySetId + ": " + GENERIC_XACML_ERROR_MESSAGE);
            }

            somethingHasBeenSelected = true;
        }
        
        return somethingHasBeenSelected;
    }

    @SuppressWarnings("static-access")
    @Override
    protected Options defineCommandOptions() {
        Options options = new Options();

        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_XACML_DESCRIPTION)
                .withLongOpt(OPT_SHOW_XACML_LONG).create());
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_IDS_DESCRIPTION).withLongOpt(OPT_SHOW_IDS_LONG)
                .create(OPT_SHOW_IDS));
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_SHOW_RULES_ID_DESCRIPTION).withLongOpt(OPT_SHOW_RULES_ID_LONG)
                .create(OPT_SHOW_RULES_ID));
        options.addOption(OptionBuilder.hasArg(false).withDescription(OPT_LIST_ONE_BY_ONE_DESCRIPTION).withLongOpt(
                OPT_LIST_ONE_BY_ONE_LONG).create(OPT_LIST_ONE_BY_ONE));

        return options;
    }

    @Override
    protected int executeCommand(CommandLine commandLine) throws ParseException, RemoteException {
        boolean xacmlOutput = false;
        boolean showIds = false;
        boolean showRulesId = false;
        boolean getPoliciesOneByOne = false;

        if (commandLine.hasOption(OPT_SHOW_XACML_LONG)) {
            xacmlOutput = true;
        }

        if (commandLine.hasOption(OPT_LIST_ONE_BY_ONE)) {
            getPoliciesOneByOne = true;
        }

        if (commandLine.hasOption(OPT_SHOW_IDS)) {
            showIds = true;
        }
        
        if (commandLine.hasOption(OPT_SHOW_RULES_ID_LONG)) {
            showRulesId = true;
        }

        XACMLPolicyCLIUtils.initOpenSAML();
        
        PAPPolicyIterator policyIter = new PAPPolicyIterator(xacmlPolicyMgmtClient, null, getPoliciesOneByOne);

        policyIter.init();

        boolean policiesFound;

        policiesFound = listPolicies(policyIter, showIds, showRulesId, xacmlOutput);

        if (!policiesFound) {
            printOutputMessage("No policies has been found.");
        }

        return ExitStatus.SUCCESS.ordinal();
    }
}
